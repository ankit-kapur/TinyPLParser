package util;

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
		if (leftOperand != null) {
			if (leftOperand instanceof CompoundExpression) {
				compoundExpression = (CompoundExpression) leftOperand;
			} else {
				String compoundString = null;
				if (leftOperand instanceof Character)
					compoundString = String.valueOf((Character) leftOperand);
				else if (leftOperand instanceof String)
					compoundString = (String) leftOperand;
				else
					System.err.println("Problem in CompoundExpression");

				compoundExpression = new CompoundExpression(compoundString);
			}
		}

		this.leftOperand = compoundExpression;
	}

	public void setRightOperand(Object rightOperand) {

		CompoundExpression compoundExpression = null;
		if (rightOperand != null) {
			if (rightOperand instanceof CompoundExpression) {
				compoundExpression = (CompoundExpression) rightOperand;
			} else {
				String compoundString = null;
				if (rightOperand instanceof Character)
					compoundString = String.valueOf((Character) rightOperand);
				else if (rightOperand instanceof String)
					compoundString = (String) rightOperand;
				else
					System.err.println("Problem in CompoundExpression");

				compoundExpression = new CompoundExpression(compoundString);
			}
		}

		this.rightOperand = compoundExpression;
	}

	public CompoundExpression getRightOperand() {
		return rightOperand;
	}
}