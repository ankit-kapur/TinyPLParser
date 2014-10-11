package dao;

public class CompoundExpression {
	char center;
	CompoundExpression leftOperand;
	CompoundExpression rightOperand;

	public CompoundExpression() {

	}

	public CompoundExpression(Character center) {
		this.center = center;
		leftOperand = null;
		rightOperand = null;
	}

	public char getCenter() {
		return center;
	}

	public void setOperator(char operator) {
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
			compoundExpression = new CompoundExpression((Character) leftOperand);			
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
			compoundExpression = new CompoundExpression((Character) rightOperand);
		}

		this.rightOperand = compoundExpression;
	}
}