package cn.sast.dataflow.analysis

interface IBugReporter {
   fun report(type: String, element: Any)
}
