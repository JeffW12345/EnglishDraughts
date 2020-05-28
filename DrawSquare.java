import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

/*
 * Creates piece images and allows them to be changed whenever
 *
 */

public class DrawSquare extends JPanel {
	private EState state;

	DrawSquare(EState state) {
		this.state = state;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (state == EState.redman) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(Color.RED);
			g2.fill(new Ellipse2D.Double(0, 10, 25, 25));
			g2.setPaint(Color.RED);
			g2.draw(new Ellipse2D.Double(0, 10, 25, 25));
		}

		if (state == EState.whiteman) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(Color.WHITE);
			g2.fill(new Ellipse2D.Double(0, 10, 25, 25));
			g2.setPaint(Color.WHITE);
			g2.draw(new Ellipse2D.Double(0, 10, 25, 25));
		}
		if (state == EState.redman_king) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(Color.RED);
			g2.fill(new Ellipse2D.Double(0, 10, 25, 25));
			g2.setPaint(Color.RED);
			g2.draw(new Ellipse2D.Double(0, 10, 25, 25));
			Font font = new Font("Verdana", Font.BOLD, 20);
			g2.setFont(font);
			g2.setColor(Color.white);
			g2.drawString("K", (getWidth() / 2) - 7, (getHeight() / 2) + 7);
		}

		if (state == EState.whiteman_king) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(Color.WHITE);
			g2.fill(new Ellipse2D.Double(0, 10, 25, 25));
			g2.setPaint(Color.WHITE);
			g2.draw(new Ellipse2D.Double(0, 10, 25, 25));
			Font font = new Font("Verdana", Font.BOLD, 20);
			g2.setFont(font);
			g2.setColor(Color.black);
			g2.drawString("K", (getWidth() / 2) - 7, (getHeight() / 2) + 7);
		}
	}

	public void setState(EState state) {
		this.state = state;

	}

	public static DrawSquare getFor(JComponent component) {
		return (DrawSquare) component.getComponents()[0];
	}
}
