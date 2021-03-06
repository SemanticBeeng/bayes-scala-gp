# Gaussian Process Classification

Notes for learning phase:
  * Log likelihood is approximated by loo-cv based on cavity distributions. See 5.5.3 in Carl Edward Rasmussen and Christopher K. I. Williams. Gaussian Processes for Machine Learning, 2006
  * [Log likelihood gradient derivation](https://github.com/danielkorzekwa/bayes-scala-gp/blob/master/doc/gpc/gpc-loglik.pdf)

Examples
  * [training](https://github.com/danielkorzekwa/bayes-scala-gp/blob/master/src/test/scala/dk/gp/gpc/gpcTrainTest.scala)
  * [prediction](https://github.com/danielkorzekwa/bayes-scala-gp/blob/master/src/test/scala/dk/gp/gpc/gpcPredictTest.scala)