package dk.gp.hgpc

import breeze.linalg.NotConvergedException
import breeze.linalg.DenseVector
import breeze.optimize.ApproximateGradientFunction
import breeze.optimize.LBFGS
import dk.gp.hgpc.util.calcHGPCLoglik

/**
 * Hierarchical Gaussian Process classification. Multiple Gaussian Processes for n tasks with a single shared parent GP.
 */
object hgpcTrain {

  def apply(hgpcModel: HgpcModel, maxIter: Int = 100): HgpcModel = {

    val g = calcLoglik(hgpcModel)(_)
    val diffFunc = new ApproximateGradientFunction(g)

    val initialParams = DenseVector(hgpcModel.covFuncParams.toArray :+ hgpcModel.mean)

    val optimizer = new LBFGS[DenseVector[Double]](maxIter, m = 6, tolerance = 1.0E-6)
    val optIterations = optimizer.iterations(diffFunc, initialParams).toList
    val newParams = optIterations.last.x

    val newCovFuncParams = DenseVector(newParams.toArray.dropRight(1))
    val newMean = newParams.toArray.last

    val trainedModel = hgpcModel.copy(covFuncParams = newCovFuncParams, mean = newMean)

    trainedModel

  }

  private def calcLoglik(gpcModel: HgpcModel)(params: DenseVector[Double]): Double = {

    val currCovFuncParams = DenseVector(params.toArray.dropRight(1))

    val currMean = params.toArray.last

    val currModel = gpcModel.copy(covFuncParams = currCovFuncParams, mean = currMean)
    val loglik = calcHGPCLoglik(currModel)
    -loglik
  }
}