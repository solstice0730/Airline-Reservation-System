package com.team1.airline.gui;

import com.team1.airline.dao.UserDAO;
import com.team1.airline.dao.impl.UserDAOImpl;
import com.team1.airline.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.event.ActionListener;

/**
 * [마이페이지 패널]
 * 개인정보 조회, 수정 및 회원탈퇴 기능 제공.
 */
public class MyPagePanel extends JPanel {

	private final MainApp mainApp;
	private final UserDAO userDAO;
	private User currentUser;

	private final Color PRIMARY_BLUE = new Color(0, 122, 255);
	private final Color LIGHT_GRAY_BG = new Color(245, 245, 245);
	private final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
	private final Font FONT_NAME = new Font("SansSerif", Font.BOLD, 20);
	private final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 14);
	private final Font FONT_VALUE = new Font("SansSerif", Font.PLAIN, 14);

	private JLabel nameIdLabel;
	private JLabel passportValueLabel;
	private JLabel phoneValueLabel;
	private JLabel pwMaskLabel;
	private JLabel mileageLabel; 

	public MyPagePanel(MainApp mainApp) {
		this.mainApp = mainApp;
		this.userDAO = new UserDAOImpl();

		setLayout(new BorderLayout());
		setBackground(LIGHT_GRAY_BG);
		setPreferredSize(new Dimension(800, 600));

		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createContentPanel(), BorderLayout.CENTER);
	}

	private JPanel createHeaderPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(PRIMARY_BLUE);
		panel.setBorder(new EmptyBorder(10, 15, 10, 15));

		JLabel title = new JLabel("마이페이지", SwingConstants.CENTER);
		title.setFont(FONT_TITLE);
		title.setForeground(Color.WHITE);

		JButton closeButton = new JButton("X");
		closeButton.setForeground(Color.WHITE);
		closeButton.setBackground(PRIMARY_BLUE);
		closeButton.setBorder(null);
		closeButton.setFocusPainted(false);
		closeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
		closeButton.addActionListener(e -> mainApp.showPanel("MAIN"));

		panel.add(title, BorderLayout.CENTER);
		panel.add(closeButton, BorderLayout.EAST);
		return panel;
	}

	private JPanel createContentPanel() {
		JPanel container = new JPanel(new GridBagLayout());
		container.setBackground(LIGHT_GRAY_BG);

		JPanel cardPanel = new JPanel(new GridBagLayout());
		cardPanel.setBackground(Color.WHITE);
		cardPanel.setPreferredSize(new Dimension(550, 400));
		cardPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true),
				new EmptyBorder(30, 40, 30, 40)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 4;
		gbc.anchor = GridBagConstraints.NORTH; gbc.weightx = 0;
		cardPanel.add(createProfileIcon(), gbc);

		gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.weightx = 1.0;
		nameIdLabel = new JLabel("사용자 정보 없음");
		nameIdLabel.setFont(FONT_NAME);
		cardPanel.add(nameIdLabel, gbc);

		gbc.gridy = 1;
		pwMaskLabel = new JLabel("****");
		pwMaskLabel.setFont(FONT_VALUE);
		cardPanel.add(createRow("비밀번호", pwMaskLabel, e -> openChangePasswordDialog()), gbc);

		gbc.gridy = 2;
		passportValueLabel = new JLabel("-");
		passportValueLabel.setFont(FONT_VALUE);
		cardPanel.add(createRow("여권번호", passportValueLabel, e -> openChangePassportDialog()), gbc);

		gbc.gridy = 3;
		phoneValueLabel = new JLabel("-");
		phoneValueLabel.setFont(FONT_VALUE);
		cardPanel.add(createRow("전화번호", phoneValueLabel, e -> openChangePhoneDialog()), gbc);

		gbc.gridy = 4;
		mileageLabel = new JLabel("0 P");
		mileageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
		mileageLabel.setForeground(new Color(0, 100, 0)); 

		JPanel mileageRow = createRow("마일리지", mileageLabel, null);
		cardPanel.add(mileageRow, gbc);

		gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.gridheight = 1;
		gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(30, 10, 5, 10);

		JButton saveButton = new JButton("변경사항 저장");
		saveButton.setBackground(new Color(30, 80, 180));
		saveButton.setForeground(Color.WHITE);
		saveButton.setFont(FONT_LABEL);
		saveButton.setPreferredSize(new Dimension(0, 45));
		saveButton.setFocusPainted(false);
		saveButton.addActionListener(e -> saveChangesToDB());
		cardPanel.add(saveButton, gbc);

		gbc.gridy = 6; gbc.insets = new Insets(5, 10, 10, 10);
		JButton deleteButton = new JButton("회원탈퇴");
		deleteButton.setBackground(Color.GRAY);
		deleteButton.setForeground(Color.WHITE);
		deleteButton.setFont(FONT_LABEL);
		deleteButton.setPreferredSize(new Dimension(0, 45));
		deleteButton.setFocusPainted(false);
		deleteButton.addActionListener(e -> deleteAccount());
		cardPanel.add(deleteButton, gbc);

		container.add(cardPanel);
		return container;
	}

	private JPanel createRow(String title, JLabel valueLabel, ActionListener actionListener) {
		JPanel panel = new JPanel(new BorderLayout(10, 0));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

		JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
		titleLabel.setFont(FONT_LABEL);
		titleLabel.setPreferredSize(new Dimension(80, 45));
		titleLabel.setForeground(Color.DARK_GRAY);

		valueLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

		if (actionListener != null) {
			JButton changeBtn = new JButton("변경");
			changeBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
			changeBtn.setBackground(Color.WHITE);
			changeBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			changeBtn.setPreferredSize(new Dimension(60, 30));
			changeBtn.setFocusPainted(false);
			changeBtn.addActionListener(actionListener);

			JPanel btnPanel = new JPanel(new GridBagLayout());
			btnPanel.setBackground(Color.WHITE);
			btnPanel.add(changeBtn);
			panel.add(btnPanel, BorderLayout.EAST);
		} else {
			panel.add(Box.createHorizontalStrut(70), BorderLayout.EAST);
		}
		panel.add(titleLabel, BorderLayout.WEST);
		panel.add(valueLabel, BorderLayout.CENTER);

		return panel;
	}

	private JPanel createProfileIcon() {
		JPanel iconPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(220, 230, 240));
				g2.fill(new Ellipse2D.Double(0, 0, 100, 100));
				g2.setColor(new Color(150, 170, 190));
				g2.fillOval(25, 20, 50, 50);
				g2.fillArc(10, 60, 80, 80, 0, 180);
			}
		};
		iconPanel.setPreferredSize(new Dimension(100, 100));
		iconPanel.setBackground(Color.WHITE);
		return iconPanel;
	}

    public void setUserInfo(User user) {
        this.currentUser = user;
        if (user != null) {
            nameIdLabel.setText(user.getUserName() + " ( " + user.getUserId() + " )");
            passportValueLabel.setText(user.getPassportNumber());
            phoneValueLabel.setText(user.getPhone());
            mileageLabel.setText(String.format("%,d P", user.getMileage()));
        } else {
            nameIdLabel.setText("사용자 정보 없음");
            passportValueLabel.setText("-");
            phoneValueLabel.setText("-");
            mileageLabel.setText("0 P");
        }
    }

	private void openChangePasswordDialog() {
		if (currentUser == null) {
			JOptionPane.showMessageDialog(this, "사용자 정보가 로드되지 않았습니다.");
			return;
		}
		JPasswordField pf = new JPasswordField();
		int result = JOptionPane.showConfirmDialog(this, pf, "새로운 비밀번호를 입력하세요", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String newPass = new String(pf.getPassword());
			if (!newPass.isBlank()) {
				currentUser.setPassword(newPass);
				JOptionPane.showMessageDialog(this, "비밀번호가 임시 변경되었습니다.\n[변경사항 저장]을 눌러야 반영됩니다.");
			}
		}
	}

	private void openChangePassportDialog() {
		if (currentUser == null) {
			JOptionPane.showMessageDialog(this, "사용자 정보가 로드되지 않았습니다.");
			return;
		}
		String newPassport = JOptionPane.showInputDialog(this, "새로운 여권번호를 입력하세요:", currentUser.getPassportNumber());
		if (newPassport != null && !newPassport.isBlank()) {
			currentUser.setPassportNumber(newPassport);
			passportValueLabel.setText(newPassport);
		}
	}

	private void openChangePhoneDialog() {
		if (currentUser == null) {
			JOptionPane.showMessageDialog(this, "사용자 정보가 로드되지 않았습니다.");
			return;
		}
		String newPhone = JOptionPane.showInputDialog(this, "새로운 전화번호를 입력하세요:", currentUser.getPhone());
		if (newPhone != null && !newPhone.isBlank()) {
			currentUser.setPhone(newPhone);
			phoneValueLabel.setText(newPhone);
		}
	}

	private void saveChangesToDB() {
		if (currentUser == null) return;
		userDAO.updateUser(currentUser);
		JOptionPane.showMessageDialog(this, "모든 변경사항이 저장되었습니다.");
	}

	private void deleteAccount() {
		if (currentUser == null) return;
		int confirm = JOptionPane.showConfirmDialog(this, "정말로 탈퇴하시겠습니까?\n탈퇴 시 모든 정보가 삭제됩니다.", "회원 탈퇴",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			userDAO.deleteUser(currentUser.getUserId());
			JOptionPane.showMessageDialog(this, "회원 탈퇴가 완료되었습니다.");
			mainApp.showPanel("LOGIN");
		}
	}
}