package Urna;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class BocaDeUrnasApp {

    private static List<String> candidatos = new ArrayList<>();
    private static JPanel panelCandidatosAdmin;
    private static JPanel panelCandidatosUsuario;
    private static JPanel panelEstadisticas;
    private static JTextField txtNombreCandidato;
    private static JTextField txtPartidoCandidato;
    private static String pinAdmin = "1234";

    private static Map<String, Map<String, Integer>> votosPorProvincia = new HashMap<>();

    private static Map<String, Color> coloresCandidatos = new HashMap<>();
    private static Map<String, Integer> votosPorCandidato = new HashMap<>();

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Boca de Urna");

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelAdmin = crearPanelAdmin();
        JPanel panelUsuario = crearPanelUsuario();

        tabbedPane.addTab("Gestionar", null, panelAdmin, "Panel de Administrador");
        tabbedPane.addTab("Usuario", null, panelUsuario, "Panel de Usuarios");

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelPrincipal.add(tabbedPane, gbc);

        ventana.getContentPane().add(panelPrincipal);

        ventana.setMinimumSize(new Dimension(480, 400));
        ventana.pack();

        ventana.setLocationRelativeTo(null);

        ventana.setVisible(true);
    }

    private static JPanel crearPanelAdmin() {
        JPanel panelAdmin = new JPanel(new BorderLayout());

        JLabel labelTitulo = new JLabel("Agregar Candidatos");
        labelTitulo.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelTitulo.setHorizontalAlignment(JLabel.CENTER);
        panelAdmin.add(labelTitulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel();
        panelCandidatosAdmin = new JPanel();
        panelEstadisticas = new JPanel();

        txtNombreCandidato = new JTextField(15);
        txtNombreCandidato.setBounds(27, 45, 126, 20);
        txtPartidoCandidato = new JTextField(15);
        txtPartidoCandidato.setBounds(158, 45, 126, 20);

        JButton btnAgregarCandidato = new JButton("Agregar Candidato");
        btnAgregarCandidato.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btnAgregarCandidato.setBounds(291, 44, 139, 23);
        btnAgregarCandidato.addActionListener(e -> agregarCandidato());

        JButton btnVerResultados = new JButton("Ver Resultados");
        btnVerResultados.addActionListener(e -> mostrarResultados());
        panelFormulario.setLayout(null);

        panelFormulario.add(txtNombreCandidato);
        panelFormulario.add(txtPartidoCandidato);
        panelFormulario.add(btnAgregarCandidato);

        panelAdmin.add(panelFormulario, BorderLayout.CENTER);
        
        JLabel lblNewLabel = new JLabel("Nombre del Candidato");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(27, 16, 126, 23);
        panelFormulario.add(lblNewLabel);
        
        JLabel lblPartidoDelCandidato = new JLabel("Organización Política del Candidato");
        lblPartidoDelCandidato.setHorizontalAlignment(SwingConstants.CENTER);
        lblPartidoDelCandidato.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblPartidoDelCandidato.setBounds(158, 16, 126, 23);
        panelFormulario.add(lblPartidoDelCandidato);
        panelAdmin.add(panelCandidatosAdmin, BorderLayout.SOUTH);
        panelAdmin.add(panelEstadisticas, BorderLayout.EAST);

        return panelAdmin;
    }

    private static JPanel crearPanelUsuario() {
        JPanel panelUsuario = new JPanel(new BorderLayout());

        JLabel labelTitulo = new JLabel("Votar por Candidato");
        labelTitulo.setHorizontalAlignment(JLabel.CENTER);
        panelUsuario.add(labelTitulo, BorderLayout.NORTH);

        JPanel panelVotacion = new JPanel();
        panelCandidatosUsuario = new JPanel();
        panelEstadisticas = new JPanel();

        JButton btnAccesoAdmin = new JButton("Acceder como Administrador");
        btnAccesoAdmin.addActionListener(e -> solicitarPIN());

        panelUsuario.add(btnAccesoAdmin, BorderLayout.CENTER);
        panelUsuario.add(panelVotacion, BorderLayout.CENTER);
        panelUsuario.add(panelCandidatosUsuario, BorderLayout.SOUTH);
        panelUsuario.add(panelEstadisticas, BorderLayout.EAST);

        return panelUsuario;
    }

    private static void solicitarPIN() {
        String ingresoPIN = JOptionPane.showInputDialog(null, "Ingrese el PIN de administrador:", "Acceso de Administrador", JOptionPane.PLAIN_MESSAGE);
        if (ingresoPIN != null && ingresoPIN.equals(pinAdmin)) {
            JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, panelCandidatosUsuario);
            tabbedPane.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(null, "PIN incorrecto. Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void agregarCandidato() {
        String nombre = txtNombreCandidato.getText();
        String partido = txtPartidoCandidato.getText();

        if (!nombre.isEmpty() && !partido.isEmpty()) {
            String candidato = nombre + " (" + partido + ")";
            candidatos.add(candidato);
            coloresCandidatos.put(candidato, obtenerColorAleatorio());
            votosPorCandidato.put(candidato, 0);
            inicializarVotosPorProvincia(candidato);
            agregarCardCandidatoAdmin(candidato);
            agregarCardCandidatoUsuario(candidato);

            txtNombreCandidato.setText("");
            txtPartidoCandidato.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese el nombre y la organizacion politica del candidato.");
        }
    }

    private static void inicializarVotosPorProvincia(String candidato) {
        for (String provincia : provincias) {
            votosPorProvincia.computeIfAbsent(candidato, k -> new HashMap<>()).put(provincia, 0);
        }
    }

    private static void agregarCardCandidatoAdmin(String candidato) {
        JPanel cardCandidato = new JPanel();
        JButton btnCandidato = new JButton(candidato);
        btnCandidato.setBackground(coloresCandidatos.get(candidato));
        cardCandidato.add(btnCandidato);
        panelCandidatosAdmin.add(cardCandidato);
        panelCandidatosAdmin.revalidate();
    }

    private static void agregarCardCandidatoUsuario(String candidato) {
        JPanel cardCandidato = new JPanel();
        JButton btnVotar = new JButton("Votar");
        btnVotar.addActionListener(e -> votarCandidato(candidato));
        cardCandidato.add(new JLabel(candidato));
        cardCandidato.add(btnVotar);
        panelCandidatosUsuario.add(cardCandidato);
        panelCandidatosUsuario.revalidate();
    }

    private static void mostrarResultados() {
        actualizarEstadisticasAdmin();
        actualizarEstadisticasPorProvincia();
        mostrarLider();
        agregarBotonFinalizar();
    }

    private static void votarCandidato(String candidato) {
        String provincia = JOptionPane.showInputDialog(null, "Ingrese la provincia:", "Votar por Provincia", JOptionPane.PLAIN_MESSAGE);
        if (provincia != null && !provincia.isEmpty() && provincias.contains(provincia)) {
            votosPorCandidato.put(candidato, votosPorCandidato.get(candidato) + 1);
            votarCandidatoPorProvincia(candidato, provincia);
            JOptionPane.showMessageDialog(null, "Votaste por: " + candidato + " en la provincia de " + provincia);
            actualizarEstadisticasAdmin();
            actualizarEstadisticasPorProvincia();
            mostrarLider();
            agregarBotonFinalizar();
        } else {
            JOptionPane.showMessageDialog(null, "Provincia incorrecta. Introduce una provincia válida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void votarCandidatoPorProvincia(String candidato, String provincia) {
        votosPorProvincia.get(candidato).put(provincia, votosPorProvincia.get(candidato).get(provincia) + 1);
    }

    private static void actualizarEstadisticasAdmin() {
        panelEstadisticas.removeAll();

        JLabel labelEstadisticas = new JLabel("Estadísticas Descriptivas");
        JTextArea areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);

        for (String candidato : candidatos) {
            int votos = votosPorCandidato.get(candidato);
            areaEstadisticas.append(candidato + ": " + votos + " votos\n");
        }

        panelEstadisticas.add(labelEstadisticas, BorderLayout.NORTH);
        panelEstadisticas.add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);

        panelEstadisticas.revalidate();
        panelEstadisticas.repaint();
    }

    private static void actualizarEstadisticasPorProvincia() {
        panelEstadisticas.removeAll();

        JLabel labelEstadisticas = new JLabel("Estadísticas Descriptivas por Provincia");
        JTextArea areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);

        for (String provincia : provincias) {
            areaEstadisticas.append("Provincia: " + provincia + "\n");

            for (String candidato : candidatos) {
                int votos = votosPorProvincia.get(candidato).get(provincia);
                areaEstadisticas.append(candidato + ": " + votos + " votos\n");
            }

            areaEstadisticas.append("\n");
        }

        panelEstadisticas.add(labelEstadisticas, BorderLayout.NORTH);
        panelEstadisticas.add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);

        panelEstadisticas.revalidate();
        panelEstadisticas.repaint();
    }

    private static void mostrarLider() {
        int maxVotos = 0;
        String lider = "";

        for (String candidato : candidatos) {
            int votos = votosPorCandidato.get(candidato);
            if (votos > maxVotos) {
                maxVotos = votos;
                lider = candidato;
            }
        }

        JLabel labelLider = new JLabel("Candidato Líder: " + lider + " con " + maxVotos + " votos");
        panelEstadisticas.add(labelLider, BorderLayout.SOUTH);

        panelEstadisticas.revalidate();
        panelEstadisticas.repaint();
    }

    private static void agregarBotonFinalizar() {
        JButton btnFinalizar = new JButton("Salir");
        btnFinalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                determinarGanador();
            }
        });
        panelEstadisticas.add(btnFinalizar, BorderLayout.SOUTH);

        panelEstadisticas.revalidate();
        panelEstadisticas.repaint();
    }

    private static void determinarGanador() {
        int maxVotos = 0;
        String ganador = "";

        for (String candidato : candidatos) {
            int votos = votosPorCandidato.get(candidato);
            if (votos > maxVotos) {
                maxVotos = votos;
                ganador = candidato;
            }
        }

        JOptionPane.showMessageDialog(null, "¡El candidato con mas voto es: " + ganador + " con " + maxVotos + " votos.");

        System.exit(0);
    }

    private static Color obtenerColorAleatorio() {
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }

    private static List<String> provincias = Arrays.asList("manabi", "pichincha", "guayas");
}