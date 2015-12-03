package dk.gp.gpc.factorgraph2

trait DoubleFactor[V1, V2] {
  private val msgV1 = Message[V1]
  private val msgV2 = Message[V2]

  msgV1.set(getInitialMsgV1())
  msgV2.set(getInitialMsgV2())

  getV1().addMessage(msgV1)
  getV2().addMessage(msgV2)

  def getMsgV1(): Message[V1] = msgV1
  def updateMsgV1() = msgV1.set(calcNewMsgV1())

  def getMsgV2(): Message[V2] = msgV2
  def updateMsgV2() = msgV2.set(calcNewMsgV2())

  /**
   * Definitions
   */

  def getV1(): Variable[V1]
  def getInitialMsgV1(): V1
  def calcNewMsgV1(): V1

  def getV2(): Variable[V2]
  def getInitialMsgV2(): V2
  def calcNewMsgV2(): V2

}