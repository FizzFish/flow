package cn.sast.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.ForwardingLoadingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.io.Closeable;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import org.apache.commons.compress.archivers.zip.ZipFile;
import soot.util.SharedCloseable;

public class SharedZipFileCacheWrapper {
   private final SharedZipFileCacheWrapper.SharedResourceCache<Path, ZipFile> cache;

   public SharedZipFileCacheWrapper(int initSize, int maxSize) {
      this.cache = new SharedZipFileCacheWrapper.SharedResourceCache<>(initSize, maxSize, new CacheLoader<Path, ZipFile>() {
         public ZipFile load(Path archivePath) throws Exception {
            return new ZipFile(archivePath);
         }
      });
   }

   public SharedCloseable<ZipFile> getRef(Path archivePath) throws ExecutionException {
      return this.cache.get(archivePath);
   }

   public void invalidateAll() {
      this.cache.invalidateAll();
   }

   private static class SharedResourceCache<K, V extends Closeable> extends ForwardingLoadingCache<K, SharedCloseable<V>> {
      private final LoadingCache<K, SharedCloseable<V>> delegate;
      private final SharedZipFileCacheWrapper.SharedResourceCache.DelayedRemovalListener<K, SharedCloseable<V>> removalListener = new SharedZipFileCacheWrapper.SharedResourceCache.DelayedRemovalListener<>();

      public SharedResourceCache(int initSize, int maxSize, final CacheLoader<K, V> loader) {
         CacheBuilder<K, SharedCloseable<V>> builder = CacheBuilder.newBuilder()
            .concurrencyLevel(OS.INSTANCE.getMaxThreadNum())
            .expireAfterAccess(15L, TimeUnit.SECONDS)
            .removalListener(this.removalListener);
         if (initSize > 0) {
            builder = builder.initialCapacity(initSize);
         }

         if (maxSize > 0) {
            builder = builder.maximumSize(maxSize);
         }

         this.delegate = builder.build(new CacheLoader<K, SharedCloseable<V>>() {
            public SharedCloseable<V> load(K key) throws Exception {
               return new SharedCloseable((Closeable)loader.load(key));
            }
         });
      }

      protected final LoadingCache<K, SharedCloseable<V>> delegate() {
         return this.delegate;
      }

      public final SharedCloseable<V> get(K key) throws ExecutionException {
         this.removalListener.delay(key);

         SharedCloseable var2;
         try {
            var2 = ((SharedCloseable)super.get(key)).acquire();
         } finally {
            this.removalListener.release(key);
         }

         return var2;
      }

      private static class DelayedRemovalListener<K, V extends SharedCloseable<?>> implements RemovalListener<K, V> {
         private static final BiFunction<Object, Integer, Integer> INC = new BiFunction<Object, Integer, Integer>() {
            public Integer apply(Object t, Integer u) {
               return u == null ? 1 : u + 1;
            }
         };
         private static final BiFunction<Object, Integer, Integer> DEC = new BiFunction<Object, Integer, Integer>() {
            public Integer apply(Object t, Integer u) {
               return u == 1 ? null : u - 1;
            }
         };
         private final ConcurrentHashMap<K, Integer> delayed = new ConcurrentHashMap<>();
         private final Queue<RemovalNotification<K, V>> delayQueue = new ConcurrentLinkedQueue<>();

         public void onRemoval(RemovalNotification<K, V> rn) {
            this.process();
            this.removeOrEnqueue(rn, this.delayQueue);
         }

         public void delay(K key) {
            Integer val = this.delayed.compute(key, INC);

            assert val != null && val > 0;
         }

         public void release(K key) {
            Integer val = this.delayed.compute(key, DEC);

            assert val == null || val > 0;

            this.process();
         }

         private void process() {
            Queue<RemovalNotification<K, V>> delayFurther = new LinkedList<>();

            RemovalNotification<K, V> rn;
            while ((rn = this.delayQueue.poll()) != null) {
               this.removeOrEnqueue(rn, delayFurther);
            }

            this.delayQueue.addAll(delayFurther);
         }

         private void removeOrEnqueue(RemovalNotification<K, V> rn, Queue<RemovalNotification<K, V>> q) {
            if (this.delayed.containsKey(rn.getKey())) {
               q.offer(rn);
            } else {
               V val = (V)((SharedCloseable)rn.getValue());

               assert val != null;

               val.release();
            }
         }
      }
   }
}
