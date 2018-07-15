package com.github.hackeklotz;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class PylintConfiguration implements Configurable {

	private JTextField pylintPathTextField;
	private JTextField commandLineParamsTextField;

	@Override
	public String getDisplayName() {
		return "Pylint Plugin";
	}

	@Override
	public JComponent createComponent() {
		PropertiesComponent properties = PropertiesComponent.getInstance();
		String pylintPath = properties.getValue("pylint.path");
		String commandLineParameters = properties.getValue("pylint.commandLineParameters");

		return createRootPanel(pylintPath, commandLineParameters);
	}

	private JComponent createRootPanel(String pylintPath, String commandLineParameters) {
		JPanel rootPanel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();		
		constraints.ipadx = 5;
		constraints.ipady = 5;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;		

		JLabel pylintPathLabel = new JLabel("Path to Pylint:");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		rootPanel.add(pylintPathLabel, constraints);

		pylintPathTextField = new JTextField(pylintPath);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		rootPanel.add(pylintPathTextField, constraints);
		
		JLabel commandLineParamLabel = new JLabel("Command line parameters:");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		rootPanel.add(commandLineParamLabel, constraints);

		commandLineParamsTextField = new JTextField(commandLineParameters);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		rootPanel.add(commandLineParamsTextField, constraints);

		// dummy label to take up all the vertical space
		constraints.weighty = 1;
		rootPanel.add(new JLabel(""), constraints);

		return rootPanel;
	}

	@Override
	public boolean isModified() {
		return true;
	}

	@Override
	public void apply() throws ConfigurationException {
		PropertiesComponent properties = PropertiesComponent.getInstance();
		properties.setValue("pylint.path", pylintPathTextField.getText());
		properties.setValue("pylint.commandLineParameters", commandLineParamsTextField.getText());
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Configuration Dialog");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		PylintConfiguration conf = new PylintConfiguration();
		JComponent createComponent = conf.createRootPanel("dummy", "");
		frame.getContentPane().add(createComponent);

		frame.setSize(600, 400);
		frame.setVisible(true);
	}
}
