package fpl;

public class CompoundExpression {
	String center;
	CompoundExpression leftOperand;
	CompoundExpression rightOperand;

	public CompoundExpression() {

	}

	public CompoundExpression(String center) {
		this.center = center;
		leftOperand = null;
		rightOperand = null;
	}

	public String getCenter() {
		return center;
	}

	public void setOperator(String operator) {
		this.center = operator;
	}

	public CompoundExpression getLeftOperand() {
		return leftOperand;
	}

	public void setLeftOperand(Object leftOperand) {
		CompoundExpression compoundExpression = null;
		if (leftOperand instanceof CompoundExpression) {
			compoundExpression = (CompoundExpression) leftOperand;
		} else {
			compoundExpression = new CompoundExpression((String) leftOperand);			
		}

		this.leftOperand = compoundExpression;
	}

	public CompoundExpression getRightOperand() {
		return rightOperand;
	}

	public void setRightOperand(Object rightOperand) {

		CompoundExpression compoundExpression = null;
		if (rightOperand instanceof CompoundExpression) {
			compoundExpression = (CompoundExpression) rightOperand;
		} else {
			compoundExpression = new CompoundExpression((String) rightOperand);
		}

		this.rightOperand = compoundExpression;
	}
}