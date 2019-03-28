package jp.co.ncast.form;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainForm extends JFrame implements ActionListener {

	private JPanel contentPane;
	private static final String act_movePMG = "PMG";
	private static final String act_moveDF = "DF";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm frame = new MainForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainForm() {
		setTitle("SalesforceMetaUtility");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnProfileMetaGenerate = new JButton("プロファイルメタ生成");
		btnProfileMetaGenerate.setBounds(31, 35, 161, 45);
		btnProfileMetaGenerate.addActionListener(this);
		btnProfileMetaGenerate.setActionCommand(act_movePMG);
		contentPane.add(btnProfileMetaGenerate);

		JButton btn_DisableFunction = new JButton("機能無効化");
		btn_DisableFunction.addActionListener(this);
		btn_DisableFunction.setActionCommand(act_moveDF);
		btn_DisableFunction.setBounds(237, 35, 161, 45);
		contentPane.add(btn_DisableFunction);
	}

	// ボタン押下処理
	public void actionPerformed(ActionEvent e) {

		if (act_movePMG.equals(e.getActionCommand())) {

		} else if (act_moveDF.equals(e.getActionCommand())) {

		}
	}
}
