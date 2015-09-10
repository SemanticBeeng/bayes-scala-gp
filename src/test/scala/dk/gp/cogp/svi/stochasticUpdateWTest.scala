package dk.gp.cogp.svi

import java.io.File
import org.junit._
import org.junit.Assert._
import breeze.linalg._
import dk.gp.cogp.lb.LowerBound
import dk.gp.cogp.testutils.createCogpToyModel
import dk.gp.cogp.lb.calcLBLoglik

class stochasticUpdateWTest {

  @Test def test_5_data_points = {

    val data = csvread(new File("src/test/resources/cogp/cogp_no_missing_points.csv"))(0 to 4, ::)
    val x = data(::, 0).toDenseMatrix.t
    val y = data(::, 1 to 2)

    val model = createCogpToyModel(x, y)

    val (newW, newWDelta) = stochasticUpdateW(LowerBound(model, x), y)

    val newModel = model.copy(w = newW)

    val loglik = calcLBLoglik(LowerBound(newModel, x), y)
    assertEquals(-121201.056728, loglik, 0.000001)

  }

  @Test def test_40_data_points = {

    val data = csvread(new File("src/test/resources/cogp/cogp_no_missing_points.csv"))(0 to 39, ::)
    val x = data(::, 0).toDenseMatrix.t
    val y = data(::, 1 to 2)

    val model = createCogpToyModel(x, y)

    val (newW, newWDelta) = stochasticUpdateW(LowerBound(model, x), y)

    val newModel = model.copy(w = newW)

    val loglik = calcLBLoglik(LowerBound(newModel, x),  y)
    assertEquals(-9.210001189439e7, loglik, 0.0001)

  }

}