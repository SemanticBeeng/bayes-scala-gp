package dk.gp.hgpc.util

import dk.bayes.dsl.factor.DoubleFactor
import dk.bayes.math.gaussian.canonical.DenseCanonicalGaussian
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import dk.gp.gp.gpPredict
import dk.gp.gp.gpPredictSingle
import dk.bayes.dsl.variable.gaussian.multivariate.MultivariateGaussian
import dk.gp.gpc.util.createLikelihoodVariables
import dk.bayes.infer.epnaivebayes.EPNaiveBayesFactorGraph

trait TaskFactor extends DoubleFactor[DenseCanonicalGaussian, Any] {

  this: TaskVariable =>

  val initFactorMsgUp: DenseCanonicalGaussian = {
    val m = DenseVector.zeros[Double](this.uVariable.m.size)
    val v = DenseMatrix.eye[Double](this.uVariable.m.size) * 1000d
    DenseCanonicalGaussian(m, v)
  }

  def calcYFactorMsgUp(uPosterior: DenseCanonicalGaussian, oldFactorMsgUp: DenseCanonicalGaussian): Option[DenseCanonicalGaussian] = {
   
    val uVarMsgDown = uPosterior / oldFactorMsgUp
    
    val taskXPrior = gpPredictSingle(this.taskX,dk.gp.math.MultivariateGaussian(uVarMsgDown.mean,uVarMsgDown.variance),this.model.u,this.model.covFunc,this.model.covFuncParams,this.model.mean)
    
      val taskXPriorVariable = MultivariateGaussian(taskXPrior.m,taskXPrior.v)
      
    val yVariables = createLikelihoodVariables(taskXPriorVariable, this.taskY)

    val factorGraph = EPNaiveBayesFactorGraph(taskXPriorVariable, yVariables, true)
    factorGraph.calibrate(maxIter = 10, threshold = 1e-4)
    val taskXPosteriorVariable = factorGraph.getPosterior().asInstanceOf[DenseCanonicalGaussian]
    
    val taskXVarMsgUp = taskXPosteriorVariable/DenseCanonicalGaussian(taskXPriorVariable.m,taskXPriorVariable.v)
    
  //  val xFactorCanon = DenseCanonicalGaussian
    
    throw new UnsupportedOperationException("Not implemented")
  }
}