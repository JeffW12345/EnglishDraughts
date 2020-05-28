
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/*
 * The GUI. Contains action listeners.
 */

public class DraughtsBoardView implements ActionListener {

	JFrame frame;
	private JPanel leftPanel, rightPanel, userInfoPanel;
	private JButton offerNewGameButton, acceptNewGameButton, offerDrawButton,
			acceptDrawButton, resignButton;
	private final JButton square[][] = new JButton[8][8];
	private final Font messagesFont = new Font("Aerial", Font.BOLD, 14);
	private String messageToServer;
	private JLabel lblTopMessage, lblMiddleMessage, lblBottomMessage;
	private String bottomLineMessage, middleLineMessage, topLineMessage;
	private final ClientController controller;

	DraughtsBoardView(ClientController controller) {
		this.controller = controller;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int column = 0; column < 8; column++) {
			for (int row = 0; row < 8; row++) {
				if (e.getSource() == square[column][row]) {
					controller.squareClicked(column, row);
				}
			}
		}

		if (e.getSource() == offerNewGameButton) {
			controller.offerNewGameButtonPressed();
		}
		if (e.getSource() == acceptNewGameButton) {
			controller.acceptNewGameButtonPressed();
		}
		if (e.getSource() == offerDrawButton) {
			controller.offerDrawButtonPressed();
		}
		if (e.getSource() == acceptDrawButton) {
			controller.acceptDrawButtonPressed();
		}
		if (e.getSource() == resignButton) {
			controller.resignButtonPressed();
		}

	}

	void addRedKing(int col, int row) {
		DrawSquare.getFor(square[col][row]).setState(EState.redman_king);
	}

	void addRedMan(int col, int row) {
		DrawSquare.getFor(square[col][row]).setState(EState.redman);
	}

	void addRedMenToBoard() {
		for (int row = 0; row < 3; row++) {
			for (int column = 1; column < 8; column += 2) {
				if ((row == 0) || (row == 2)) {
					DrawSquare.getFor(square[column][row])
							.setState(EState.redman);
				}
			}

			((DrawSquare) (square[0][1].getComponents()[0]))
					.setState(EState.redman);
			((DrawSquare) (square[2][1].getComponents()[0]))
					.setState(EState.redman);
			((DrawSquare) (square[4][1].getComponents()[0]))
					.setState(EState.redman);
			((DrawSquare) (square[6][1].getComponents()[0]))
					.setState(EState.redman);
		}
	}

	void addWhiteKing(int col, int row) {
		DrawSquare.getFor(square[col][row]).setState(EState.whiteman_king);
	}

	void addWhiteMan(int col, int row) {
		DrawSquare.getFor(square[col][row]).setState(EState.whiteman);
	}

	void addWhiteMenToBoard() {
		for (int row = 5; row < 8; row++) {
			for (int column = 0; column < 8; column += 2) {
				if ((row == 5) || (row == 7)) {
					DrawSquare.getFor(square[column][row])
							.setState(EState.whiteman);
				}
			}

			DrawSquare.getFor(square[1][6]).setState(EState.whiteman);
			((DrawSquare) (square[3][6].getComponents()[0]))
					.setState(EState.whiteman);
			((DrawSquare) (square[5][6].getComponents()[0]))
					.setState(EState.whiteman);
			((DrawSquare) (square[7][6].getComponents()[0]))
					.setState(EState.whiteman);
		}
	}

	JButton createAcceptDrawBtn() {
		acceptDrawButton = new JButton("Accept draw");
		acceptDrawButton.setFont(new java.awt.Font("Arial", Font.BOLD, 18));
		rightPanel.add(acceptDrawButton);
		acceptDrawButton.setEnabled(false);
		acceptDrawButton.setOpaque(true);
		acceptDrawButton.addActionListener(this);
		return acceptDrawButton;
	}

	JButton createAcceptNewGameBtn() {
		acceptNewGameButton = new JButton("Accept new game");
		acceptNewGameButton.setFont(new java.awt.Font("Arial", Font.BOLD, 18));
		rightPanel.add(acceptNewGameButton);
		acceptNewGameButton.setEnabled(false);
		acceptNewGameButton.setOpaque(true);
		acceptNewGameButton.addActionListener(this);
		return acceptNewGameButton;
	}

	void createEmptyBoard() {
		leftPanel.setLayout(new GridLayout(8, 8));
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				square[column][row] = new JButton();
				square[column][row].setOpaque(true);
				square[column][row].addActionListener(this);
				leftPanel.add(square[column][row]);
				if (((row + column) % 2) != 0) {
					square[column][row].setBackground(Color.BLACK);
				} else {
					square[column][row].setBackground(Color.WHITE);
				}
				square[column][row].add(new DrawSquare(EState.blank));
			}
		}
	}

	JFrame createFrame() {
		frame = new JFrame("English Draughts Game");
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.setSize(1000, 500);
		frame.setLayout(new GridLayout(0, 2));
		frame.addWindowListener(controller);
		try {
			UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return frame;
	}

	JPanel createLeftPanel() {
		leftPanel = new JPanel();
		leftPanel.setBackground(Color.BLACK);
		frame.add(leftPanel);
		return leftPanel;
	}

	JButton createOfferDrawBtn() {
		offerDrawButton = new JButton("Offer draw");
		offerDrawButton.setFont(new java.awt.Font("Arial", Font.BOLD, 18));
		rightPanel.add(offerDrawButton);
		offerDrawButton.setEnabled(false);
		offerDrawButton.setOpaque(true);
		offerDrawButton.addActionListener(this);
		return offerDrawButton;
	}

	JButton createOfferNewGameBtn() {
		JButton offerNewGameButton = new JButton("Offer new game");
		offerNewGameButton.setFont(new java.awt.Font("Arial", Font.BOLD, 18));
		rightPanel.add(offerNewGameButton);
		offerNewGameButton.setOpaque(true);
		offerNewGameButton.addActionListener(this);
		return offerNewGameButton;
	}

	JButton createResignButton() {
		resignButton = new JButton("Resign");
		resignButton.setFont(new java.awt.Font("Arial", Font.BOLD, 18));
		rightPanel.add(resignButton);
		resignButton.setEnabled(false);
		resignButton.addActionListener(this);
		return resignButton;
	}

	JPanel createRightPanel() {
		rightPanel = new JPanel();
		rightPanel.setBackground(Color.decode("#FFFFCC"));
		frame.add(rightPanel);
		rightPanel.setLayout(new GridLayout(3, 0, 0, 20));
		rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		return rightPanel;
	}

	JPanel createUserInfoPanel() {
		userInfoPanel = new JPanel();
		userInfoPanel.setBackground(Color.WHITE);
		userInfoPanel.setLayout(new GridLayout(4, 0, 0, 20));
		rightPanel.add(userInfoPanel);
		return userInfoPanel;
	}

	public JButton getAcceptDrawButton() {
		return acceptDrawButton;
	}

	public JButton getAcceptNewGameButton() {
		return acceptNewGameButton;
	}

	public Scanner getMessageFromServer() {
		Scanner in = controller.clientThread.in;
		return in;
	}

	public String getMessageToServer() {
		return messageToServer;
	}

	public JButton getOfferDrawButton() {
		return offerDrawButton;
	}

	public JButton getOfferNewGameButton() {
		return offerNewGameButton;
	}

	public JButton getResignButton() {
		return resignButton;
	}

	public JButton[][] getSquare() {
		return square;
	}

	void initialSetup() {
		newBoardActions();
		controller.setWelcomeMessage();
		messagesToPlayer();
	}

	void newBoardActions() {
		frame = createFrame();
		leftPanel = createLeftPanel();
		rightPanel = createRightPanel();
		createEmptyBoard();
		addRedMenToBoard();
		addWhiteMenToBoard();
		offerNewGameButton = createOfferNewGameBtn();
		acceptNewGameButton = createAcceptNewGameBtn();
		offerDrawButton = createOfferDrawBtn();
		acceptDrawButton = createAcceptDrawBtn();
		resignButton = createResignButton();
		userInfoPanel = createUserInfoPanel();
		frame.setVisible(true);
	}

	void messagesToPlayer() {
		lblTopMessage = new JLabel();
		lblTopMessage.setFont(messagesFont);
		lblMiddleMessage = new JLabel();
		lblMiddleMessage.setFont(messagesFont);
		lblBottomMessage = new JLabel();
		lblBottomMessage.setFont(messagesFont);
		userInfoPanel.add(lblTopMessage);
		userInfoPanel.add(lblMiddleMessage);
		userInfoPanel.add(lblBottomMessage);
		lblTopMessage.setText(bottomLineMessage);
		lblMiddleMessage.setText(middleLineMessage);
		lblBottomMessage.setText(topLineMessage);
	}

	public void setBlank(int col, int row) {
		DrawSquare.getFor(square[col][row]).setState(EState.blank);
	}

	public void updateLabels() {
		lblTopMessage.setText(topLineMessage);
		lblMiddleMessage.setText(middleLineMessage);
		lblBottomMessage.setText(bottomLineMessage);
	}

	public void setBottomLineMessage(String bottomLineMessage) {
		this.bottomLineMessage = bottomLineMessage;
	}

	public void setMiddleLineMessage(String middleLineMessage) {
		this.middleLineMessage = middleLineMessage;
	}

	public void setTopLineMessage(String topLineMessage) {
		this.topLineMessage = topLineMessage;
	}
}
