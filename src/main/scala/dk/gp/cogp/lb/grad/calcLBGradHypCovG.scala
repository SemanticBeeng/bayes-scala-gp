package dk.gp.cogp.lb.grad

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.linalg.trace
import dk.gp.cov.utils.covDiagD
import breeze.numerics._
import breeze.linalg._
import dk.gp.cogp.lb.LowerBound
import dk.gp.cogp.lb.wAm

object calcLBGradHypCovG {

  def apply(j: Int, lb: LowerBound, y: DenseMatrix[Double]): DenseVector[Double] = {

    val model = lb.model
    val x = lb.x
    
    val hArray = model.h
    val gArray = model.g

    val z = model.g(j).z
    val kXZ = lb.kXZj(j)
    val kZZ = lb.kZZj(j)
    val kZZdArray = model.g(j).covFunc.covD(z,z, model.g(j).covFuncParams)
    val kXZDArray = model.g(j).covFunc.covD(x, z, model.g(j).covFuncParams)

    val kZZinv = lb.kZZjInv(j)
    val Aj = kXZ * kZZinv
    val kZX = kXZ.t

    val kXXDiagDArray = covDiagD(x, model.g(j).covFunc, model.g(j).covFuncParams)

    val beta = model.beta
    val w = model.w

    val u = model.g(j).u

    val covParamsD = (0 until model.g(j).covFuncParams.size).map { k =>

      val kZZd = kZZdArray(k)
      val kXZd = kXZDArray(k)

      val kXXDiagD = kXXDiagDArray(k)

      val AjD = kXZd * kZZinv - Aj * kZZd * kZZinv

      val logTermPart = (0 until hArray.size).map { i =>

        val Ai = lb.calcAi(i)
        val yTerm = y(::, i) - wAm(i,lb) - Ai * hArray(i).u.m
        beta(i) * w(i, j) * (yTerm.t * AjD * u.m)

      }.sum

      val tildeQPart = (0 until hArray.size).map { i =>
      
        val tilde = 0.5 * beta(i) * pow(w(i, j), 2) * sum(kXXDiagD - diag(AjD * kZX) - diag(Aj * kXZd.t))
        tilde

      }.sum

      val traceQPart = (0 until hArray.size).map { i =>
       beta(i) * trace(pow(w(i, j), 2) * u.v * (AjD.t * Aj)) //@TODO performance improvement
      }.sum

      val lklPart = 0.5 * trace(kZZinv * kZZd) - 0.5 * trace(kZZinv * kZZd * kZZinv * (u.m * u.m.t + u.v))

      logTermPart - tildeQPart - traceQPart - lklPart

    }.toArray

    DenseVector(covParamsD)
  }
}