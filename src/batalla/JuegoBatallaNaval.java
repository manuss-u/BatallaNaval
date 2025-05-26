package batalla;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import batalla.barcos.*;
import batalla.jugadores.*;

public class JuegoBatallaNaval extends JFrame {

    private static final int TAM_TABLERO = 10;
    private String nombreJugador;

    // Jugadores
    private transient JugadorHumano jugador;
    private transient JugadorMaquina maquina;

    // Tableros y botones
    private transient Tablero tableroJugador;
    private transient Tablero tableroMaquina;
    private JButton[][] botonesJugador;
    private JButton[][] botonesMaquina;
    private JPanel panelPuntuacion;
    
    private ImageIcon imgPortaaviones;
    private ImageIcon imgAcorazado;
    private ImageIcon imgCrucero;
    private ImageIcon imgSubmarino;
    private ImageIcon imgDestructor;
    private ImageIcon imgLancha;
    
    // Lógica de colocación y disparo
    private transient List<Barco> flotaJugador;
    private List<Point> disparosDisponiblesMaquina;
    private int indiceColocacion = 0;

    // UI
    private JLabel etiquetaOrientacion;
    private JToggleButton botonOrientacion;
    private JLabel etiquetaInfo;
    private List<Point> previewPoints = new ArrayList<>();
    private int aciertosJugador;
    private int aciertosMaquina;
    private int barcosHundidosJugador;
    private int barcosHundidosMaquina;

    public JuegoBatallaNaval() {
        super("Batalla Naval");
        nombreJugador = JOptionPane.showInputDialog(this, "Ingresa tu nombre:", "Jugador", JOptionPane.PLAIN_MESSAGE);
        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            nombreJugador = "Jugador";
        }
        inicializarEstadoJuego();
        inicializarInterfaz();
    }

    private void inicializarEstadoJuego() {
        // Crear tableros
        tableroJugador = new Tablero(TAM_TABLERO);
        tableroMaquina = new Tablero(TAM_TABLERO);

        // Inicializar flotas
        flotaJugador = Arrays.asList(
                new Portaaviones(), new Acorazado(), new Crucero(),
                new Submarino(), new Destructor(), new Lancha(), new Lancha());

        // Crear jugadores
        jugador = new JugadorHumano("Humano", tableroJugador, tableroMaquina, null);
        maquina = new JugadorMaquina("Máquina", tableroMaquina, tableroJugador, null);

        // Colocar flota de la máquina
        maquina.colocarFlota(Arrays.asList(
                new Portaaviones(), new Acorazado(), new Crucero(),
                new Submarino(), new Destructor(), new Lancha(), new Lancha()));

        aciertosJugador = aciertosMaquina = barcosHundidosJugador = barcosHundidosMaquina = 0;

        // Inicializar disparos de la máquina
        disparosDisponiblesMaquina = new ArrayList<>();
        for (int r = 0; r < TAM_TABLERO; r++) {
            for (int c = 0; c < TAM_TABLERO; c++) {
                disparosDisponiblesMaquina.add(new Point(r, c));
            }
        }
    }

    private void inicializarInterfaz() {
        
        imgPortaaviones = new ImageIcon(getClass().getResource("/batalla/imagenes/portaaviones.png"));
        imgAcorazado    = new ImageIcon(getClass().getResource("/batalla/imagenes/acorazado.png"));
        imgCrucero      = new ImageIcon(getClass().getResource("/batalla/imagenes/crucero.png"));
        imgSubmarino    = new ImageIcon(getClass().getResource("/batalla/imagenes/submarino.png"));
        imgDestructor   = new ImageIcon(getClass().getResource("/batalla/imagenes/destructor.png"));
        imgLancha       = new ImageIcon(getClass().getResource("/batalla/imagenes/lancha.png"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel(new GridLayout(1, 3, 10, 10));

        // Panel jugador
        JPanel panelJ = crearPanelBotones(true);
        // Panel máquina
        JPanel panelM = crearPanelBotones(false);

        // Panel de control
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
        panelControl.setPreferredSize(new Dimension(160, 0));

        etiquetaOrientacion = new JLabel("Orientación (R para cambiar):");
        etiquetaOrientacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonOrientacion = new JToggleButton("Horizontal");
        botonOrientacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonOrientacion.addItemListener(e -> {
            jugador.ponerOrientacion(botonOrientacion.isSelected() ? Orientacion.VERTICAL : Orientacion.HORIZONTAL);
            botonOrientacion.setText(botonOrientacion.isSelected() ? "Vertical" : "Horizontal");
        });

        panelControl.add(Box.createVerticalGlue());
        panelControl.add(etiquetaOrientacion);
        panelControl.add(botonOrientacion);
        panelControl.add(Box.createVerticalStrut(10));

        char inicial = flotaJugador.get(indiceColocacion).obtenerInicial();
        String nombre = flotaJugador.get(indiceColocacion).obtenerNombre();
        etiquetaInfo = new JLabel("Coloca: " + nombre + "(" + inicial + ")");
        etiquetaInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Leyenda
        JPanel panelLeyenda = new JPanel(new GridLayout(7, 1));
        panelLeyenda.setBorder(new TitledBorder("Leyenda"));
        for (Barco b : flotaJugador) {
            char letra = b.obtenerInicial();
            String nombreBarco = b.obtenerNombre();
            int tamano = b.obtenerTamano();
            panelLeyenda.add(new JLabel(letra + " = " + nombreBarco + " (" + tamano + ")", SwingConstants.CENTER));
        }
        panelLeyenda.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Puntuación
        panelPuntuacion = new JPanel(new GridLayout(4, 3, 5, 5));
        panelPuntuacion.setBorder(new TitledBorder("Puntuaciones"));
        panelPuntuacion.add(new JLabel("Métrica", SwingConstants.CENTER));
        panelPuntuacion.add(new JLabel("Manu", SwingConstants.CENTER));
        panelPuntuacion.add(new JLabel("Máquina", SwingConstants.CENTER));
        String[] met = { "Impactos:", "Barcos Hundidos:", "Puntuación:" };
        for (String m : met) {
            panelPuntuacion.add(new JLabel(m, SwingConstants.LEFT));
            panelPuntuacion.add(new JLabel("0", SwingConstants.CENTER));
            panelPuntuacion.add(new JLabel("0", SwingConstants.CENTER));
        }
        panelPuntuacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonHistorial = new JButton("Histórico");
        botonHistorial.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonHistorial.addActionListener(e -> mostrarHistorial());

        panelControl.add(Box.createVerticalGlue());
        panelControl.add(botonOrientacion);
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(etiquetaInfo);
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(panelLeyenda);
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(panelPuntuacion);
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(botonHistorial);
        panelControl.add(Box.createVerticalGlue());

        panelPrincipal.add(panelJ);
        panelPrincipal.add(panelControl);
        panelPrincipal.add(panelM);
        add(panelPrincipal, BorderLayout.CENTER);

        // Tecla R para cambiar orientación
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke("R"), "toggleOrient");
        am.put("toggleOrient", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                botonOrientacion.doClick();
            }
        });

        pack();
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel crearPanelBotones(boolean esJugador) {
        JPanel panel = new JPanel(new GridLayout(TAM_TABLERO, TAM_TABLERO));
        JButton[][] botones = new JButton[TAM_TABLERO][TAM_TABLERO];
        for (int r = 0; r < TAM_TABLERO; r++) {
            for (int c = 0; c < TAM_TABLERO; c++) {
                JButton btn = new JButton();
                btn.setBackground(ColoresBatallaNaval.AGUA);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(ColoresBatallaNaval.BORDES));
                btn.setFont(btn.getFont().deriveFont(Font.BOLD, 16f));
                final int rr = r;
                final int cc = c;
                if (esJugador) {
                    btn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            mostrarVistaPrevia(rr, cc);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            limpiarVistaPrevia();
                        }
                    });
                    btn.addActionListener(e -> {
                        limpiarVistaPrevia();
                        manejarColocacion(rr, cc);
                    });
                    botonesJugador = botones;
                } else {
                    btn.addActionListener(e -> manejarDisparo(rr, cc));
                    botonesMaquina = botones;
                }
                botones[r][c] = btn;
                panel.add(btn);
            }
        }
        // Asignar botones al jugador o máquina para disparos
        if (!esJugador) {
            maquina.botonesOponente = botonesJugador;
        } else {
            jugador.botonesOponente = botonesMaquina;
        }
        return panel;
    }

    private void mostrarVistaPrevia(int r, int c) {
        if (indiceColocacion >= flotaJugador.size()) {
            return;
        }
        limpiarVistaPrevia();
        Barco barco = flotaJugador.get(indiceColocacion);
        Barco preview = barco; // alias
        List<Point> pts = new ArrayList<>();
        boolean ok = true;
        for (int i = 0; i < preview.obtenerTamano(); i++) {
            int rr = r + (jugador.obtenerOrientacion() == Orientacion.VERTICAL ? i : 0);
            int cc = c + (jugador.obtenerOrientacion() == Orientacion.HORIZONTAL ? i : 0);
            if (rr < 0 || rr >= TAM_TABLERO || cc < 0 || cc >= TAM_TABLERO || tableroJugador.estaOcupado(rr, cc)) {
                ok = false;
                break;
            }
            pts.add(new Point(rr, cc));
        }
        for (Point p : pts) {
            botonesJugador[p.x][p.y]
                    .setBackground(ok ? ColoresBatallaNaval.PREVISTA_OK : ColoresBatallaNaval.PREVISTA_INVALIDA);
            previewPoints.add(p);
        }
    }

    private void limpiarVistaPrevia() {
        for (Point p : previewPoints) {
            JButton btn = botonesJugador[p.x][p.y];
            btn.setBackground(
                    tableroJugador.estaOcupado(p.x, p.y) ? ColoresBatallaNaval.BARCO : ColoresBatallaNaval.AGUA);
        }
        previewPoints.clear();
    }

    private void manejarColocacion(int r, int c) {
        if (indiceColocacion >= flotaJugador.size()) {
            return;
        }
        Barco barco = flotaJugador.get(indiceColocacion);
        barco.ponerOrientacion(jugador.obtenerOrientacion());
        barco.ponerPosicion(r, c);
        if (tableroJugador.ubicarBarco(barco)) {
            for (Point p : barco.obtenerPosiciones()) {
                JButton btn = botonesJugador[p.x][p.y];
                btn.setBackground(ColoresBatallaNaval.BARCO);
                btn.setText(String.valueOf(barco.obtenerInicial()));
            }
            indiceColocacion++;
            etiquetaInfo.setText(indiceColocacion < flotaJugador.size()
                    ? "Coloca: " + flotaJugador.get(indiceColocacion).obtenerInicial()
                    : "Listo");
            if (indiceColocacion == flotaJugador.size()) {
                habilitarJuego();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lugar inválido para colocar el barco");
        }
    }

    private void habilitarJuego() {
        etiquetaOrientacion.setVisible(false);
        botonOrientacion.setVisible(false);
        etiquetaInfo.setVisible(false);
        limpiarVistaPrevia();
        for (var row : botonesJugador) {
            for (var b : row) {
                b.setEnabled(false);
            }
        }
        for (var row : botonesMaquina) {
            for (var b : row) {
                b.setEnabled(true);
            }
        }
        JOptionPane.showMessageDialog(this, "¡Comienza el juego!");
    }

    private void manejarDisparo(int r, int c) {
        if (indiceColocacion < flotaJugador.size()) {
            return;
        }
        ResultadoDisparo res = tableroMaquina.recibirDisparo(r, c);
        Barco b = tableroMaquina.obtenerBarcoPorUbicacion(r, c);
        char letra = b != null ? b.obtenerInicial() : '?';
        JButton btn = botonesMaquina[r][c];
        btn.setEnabled(false);
        switch (res) {
            case FALLO -> btn.setBackground(ColoresBatallaNaval.FALLO);
            case GOLPEADO -> {
                btn.setBackground(ColoresBatallaNaval.ACIERTO);
                btn.setText(String.valueOf(letra));
                aciertosJugador++;
            }
            case HUNDIDO -> {
                btn.setBackground(ColoresBatallaNaval.HUNDIDO);
                btn.setText(String.valueOf(letra));
                aciertosJugador++;
                barcosHundidosJugador++;
            }
        }
        actualizarTablaPuntuacion();
        if (tableroMaquina.todosLosBarcosHundidos()) {
            terminarJuego(true);
        } else {
            turnoMaquina();
        }
    }

    private void turnoMaquina() {
        ResultadoDisparo res = maquina.disparar(disparosDisponiblesMaquina);
        if (res == ResultadoDisparo.GOLPEADO) {
            aciertosMaquina++;
        } else if (res == ResultadoDisparo.HUNDIDO) {
            aciertosMaquina++;
            barcosHundidosMaquina++;
        }
        actualizarTablaPuntuacion();
        if (tableroJugador.todosLosBarcosHundidos()) {
            terminarJuego(false);
        }
    }

    private void mostrarHistorial() {
        File f = new File("results.txt");
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this, "No hay historial.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                // Parsear y formatear fecha
                LocalDateTime fecha;
                try {
                    // Intentar parsear con formato personalizado
                    fecha = LocalDateTime.parse(p[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (Exception e) {
                    // Si falla, usar ISO
                    fecha = LocalDateTime.parse(p[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                sb.append("Jugador: ").append(p[0])
                        .append(", Fecha: ").append(fechaFormateada)
                        .append(", Puntaje: ").append(p[4])
                        .append(", Resultado: ").append(p[5])
                        .append("\n");

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (sb.length() == 0) {
            sb.append("No hay historial");
        }
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(400, 200));
        JOptionPane.showMessageDialog(this, sp, "Historial de Juegos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardarResultado(boolean ganoJugador) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results.txt", true)))) {
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            int puntaje = aciertosJugador + barcosHundidosJugador;
            out.println(nombreJugador + "," + fecha + "," + aciertosJugador + "," + barcosHundidosJugador + ","
                    + puntaje + "," + (ganoJugador ? "GANÓ" : "PERDIÓ"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarTablaPuntuacion() {
        Component[] c = panelPuntuacion.getComponents();
        ((JLabel) c[4]).setText(String.valueOf(aciertosJugador));
        ((JLabel) c[5]).setText(String.valueOf(aciertosMaquina));
        ((JLabel) c[7]).setText(String.valueOf(barcosHundidosJugador));
        ((JLabel) c[8]).setText(String.valueOf(barcosHundidosMaquina));
        ((JLabel) c[10]).setText(String.valueOf(aciertosJugador + barcosHundidosJugador));
        ((JLabel) c[11]).setText(String.valueOf(aciertosMaquina + barcosHundidosMaquina));
    }

    private void terminarJuego(boolean ganoJugador) {
        guardarResultado(ganoJugador);
        String msg = ganoJugador ? "¡Ganaste!" : "¡La máquina gana!";
        Object[] opt = { "Reiniciar", "Salir" };
        int sel = JOptionPane.showOptionDialog(this, msg, "Fin de partida", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, opt, opt[0]);
        if (sel == JOptionPane.YES_OPTION) {
            reiniciarJuego();
        } else {
            System.exit(0);
        }
    }

    private void reiniciarJuego() {
        inicializarEstadoJuego();
        jugador.botonesOponente = botonesMaquina;
        maquina.botonesOponente = botonesJugador;
        indiceColocacion = 0;
        char inicial = flotaJugador.get(indiceColocacion).obtenerInicial();
        String nombre = flotaJugador.get(indiceColocacion).obtenerNombre();
        etiquetaInfo.setText("Coloca: " + nombre + "(" + inicial + ")");
        etiquetaInfo.setVisible(true);
        etiquetaOrientacion.setVisible(true);
        botonOrientacion.setSelected(false);
        botonOrientacion.setText("Horizontal");
        botonOrientacion.setVisible(true);
        for (int r = 0; r < TAM_TABLERO; r++) {
            for (int c = 0; c < TAM_TABLERO; c++) {
                botonesJugador[r][c].setBackground(ColoresBatallaNaval.AGUA);
                botonesJugador[r][c].setText("");
                botonesJugador[r][c].setEnabled(true);
                botonesMaquina[r][c].setBackground(ColoresBatallaNaval.AGUA);
                botonesMaquina[r][c].setText("");
                botonesMaquina[r][c].setEnabled(false);
            }
        }
        Component[] c = panelPuntuacion.getComponents();
        for (int i : new int[] { 4, 5, 7, 8, 10, 11 }) {
            ((JLabel) c[i]).setText("0");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JuegoBatallaNaval::new);
    }
}
