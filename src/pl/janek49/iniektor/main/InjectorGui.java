package pl.janek49.iniektor.main;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class InjectorGui extends JFrame {

    private JComboBox vmListBox;
    private JComboBox versionBox;
    private JButton button;
    private JCheckBox allVms;

    public InjectorGui() {
        setTitle("MC Injector by janek49");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Wybierz instancję maszyny wirtualnej Java:");
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        getContentPane().add(lbl);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        vmListBox = new JComboBox();
        vmListBox.setMaximumSize(new Dimension(99999, 30));
        vmListBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        getContentPane().add(vmListBox);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 5)));

        allVms = new JCheckBox();
        allVms.setSelected(false);
        allVms.setText("Pokaż wszystkie maszyny");
        allVms.setAlignmentX(Component.CENTER_ALIGNMENT);

        getContentPane().add(allVms);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lbl2 = new JLabel("Wybierz wersję MCP:");
        lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);

        getContentPane().add(lbl2);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        versionBox = new JComboBox();
        versionBox.setMaximumSize(new Dimension(99999, 30));
        versionBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        getContentPane().add(versionBox);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel btns = new JPanel();

        button = new JButton("Iniekcja!");
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton reload = new JButton("Odśwież");
        reload.setAlignmentX(Component.LEFT_ALIGNMENT);
        reload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initBox();
            }
        });

        btns.add(reload);
        btns.add(button);
        getContentPane().add(btns);

        pack();
        setSize(600, getHeight());
        setLocationRelativeTo(null);
        setLocation(getX(), 10);
        setVisible(true);

        initButton();
        initBox();
        initMCPBox();
    }

    List<VirtualMachineDescriptor> vms = null;
    public static File agentFile = new File("C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\out\\artifacts\\MC_Injector_jar\\MC-Injector.jar");

    private void initButton() {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            VirtualMachine vm = VirtualMachine.attach(vms.get(vmListBox.getSelectedIndex()));
                            vm.loadAgent(agentFile.getAbsolutePath(), versionBox.getSelectedItem().toString());
                            vm.detach();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(InjectorGui.this, ex.toString(), "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }.start();
            }
        });
    }

    private void initBox() {
        vms = new ArrayList<VirtualMachineDescriptor>();
        Vector<String> items = new Vector<String>();
        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (vmd.displayName().startsWith("net.minecraft") || vmd.displayName().startsWith("org.multimc.EntryPoint") || allVms.isSelected()) {
                vms.add(vmd);
                items.add("[PID]: " + vmd.id() + " [Main]: " + vmd.displayName());
            }
        }
        vmListBox.setModel(new DefaultComboBoxModel(items));
    }

    private void initMCPBox() {
        File[] directories = new File("versions/").listFiles(File::isDirectory);

        Vector<String> items = new Vector<String>();
        for (File file : directories) {
            items.add(file.getAbsolutePath());
        }
        versionBox.setModel(new DefaultComboBoxModel(items));
    }
}
