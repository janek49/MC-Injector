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
    private JButton button;

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
        setSize(600, 150);
        setLocationRelativeTo(null);
        setVisible(true);

        initButton();
        initBox();
    }

    List<VirtualMachineDescriptor> vms = null;

    private void initButton() {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //File agentFile = new File(InjectorGui.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                            File agentFile = new File("C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\out\\artifacts\\MC_Injector_jar\\MC-Injector.jar");

                            VirtualMachine vm = VirtualMachine.attach(vms.get(vmListBox.getSelectedIndex()));
                            vm.loadAgent(agentFile.getAbsolutePath());
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
        vms = new ArrayList<>();
        Vector<String> items = new Vector<>();
        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (vmd.displayName().startsWith("net.minecraft")){
                vms.add(vmd);
                items.add("[PID]: " + vmd.id() + " [Main]: " + vmd.displayName());
            }
        }
        vmListBox.setModel(new DefaultComboBoxModel(items));
    }
}
