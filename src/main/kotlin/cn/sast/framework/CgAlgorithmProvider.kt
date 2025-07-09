package cn.sast.framework

/** 控制流图/调用图构建算法提供者 */
enum class CgAlgorithmProvider {
    /** 使用 Soot 内置 CHA/Points-To 方案 */
    Soot,

    /** 使用麒麟（七蛟）项目 */
    QiLin,
}
