
public class TestingMain {

	public static void main(String[] args) {
		MassiveBody body1 = new MassiveBody();

		body1.setMass(100000);
		body1.setRadius(15);
		body1.setxPos(10);
		body1.setyPos(100);

		MassiveBody body2 = new MassiveBody();

		body2.setMass(200000);
		body2.setRadius(20);
		body2.setxPos(50);
		body2.setyPos(100);

		int i = 0;
		while (i < 50) {

			System.out.println("Pass " + i);
			
			
			System.out.println("    Body 1 - ");
			System.out.println("       Current x Velocity:     " + body1.getxVel());
			System.out.println("       Current x Position:     " + body1.getxPos());

			System.out.println("    Body 2 - ");
			System.out.println("       Current x Velocity:     " + body2.getxVel());
			System.out.println("       Current x Position:     " + body2.getxPos());

			body1.calculateAcceleration(body2);
			body1.calculateVelocity();
			body2.calculateVelocity();
			body1.calculatePosition();
			body2.calculatePosition();
			
			i++;
		}

	}
}
