package inventario.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

public class GraficaPanel extends JPanel {

    private static final Color[] COLORES = {
        new Color(33, 97, 140), new Color(30, 132, 73), new Color(202, 111, 30),
        new Color(142, 68, 173), new Color(176, 58, 46), new Color(23, 165, 137)
    };

    private String titulo = "";
    private Map<String, Integer> datos = new LinkedHashMap<>();

    public GraficaPanel() {
        setBackground(Color.WHITE);
    }

    public void setDatos(String titulo, Map<String, Integer> datos) {
        this.titulo = titulo;
        this.datos = datos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(titulo, 15, 22);

        if (datos.isEmpty()) {
            g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            g2.drawString("No hay equipos que cumplan los filtros.", 15, 50);
            g2.dispose();
            return;
        }

        int izquierda = 50;
        int derecha = 20;
        int arriba = 40;
        int abajo = 35;
        int anchoArea = getWidth() - izquierda - derecha;
        int altoArea = getHeight() - arriba - abajo;
        if (anchoArea <= 0 || altoArea <= 0) {
            g2.dispose();
            return;
        }

        int maximo = 1;
        for (int valor : datos.values()) {
            if (valor > maximo) {
                maximo = valor;
            }
        }

        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        FontMetrics fm = g2.getFontMetrics();
        int paso = (maximo + 4) / 5;
        for (int valor = 0; valor <= maximo; valor += paso) {
            int y = arriba + altoArea - (int) ((double) valor / maximo * altoArea);
            g2.setColor(new Color(225, 225, 225));
            g2.drawLine(izquierda, y, izquierda + anchoArea, y);
            g2.setColor(Color.DARK_GRAY);
            String texto = String.valueOf(valor);
            g2.drawString(texto, izquierda - 8 - fm.stringWidth(texto), y + 4);
        }

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(izquierda, arriba, izquierda, arriba + altoArea);
        g2.drawLine(izquierda, arriba + altoArea, izquierda + anchoArea, arriba + altoArea);

        int ranura = anchoArea / datos.size();
        int barra = Math.max(10, Math.min(ranura - 24, 90));
        int posicion = 0;
        for (Map.Entry<String, Integer> par : datos.entrySet()) {
            int altura = (int) ((double) par.getValue() / maximo * altoArea);
            int x = izquierda + posicion * ranura + (ranura - barra) / 2;
            int y = arriba + altoArea - altura;
            g2.setColor(COLORES[posicion % COLORES.length]);
            g2.fillRect(x, y, barra, altura);
            g2.setColor(Color.DARK_GRAY);
            String valor = String.valueOf(par.getValue());
            g2.drawString(valor, x + (barra - fm.stringWidth(valor)) / 2, y - 4);
            String etiqueta = par.getKey();
            while (etiqueta.length() > 3 && fm.stringWidth(etiqueta) > ranura - 4) {
                etiqueta = etiqueta.substring(0, etiqueta.length() - 2);
            }
            g2.drawString(etiqueta, x + (barra - fm.stringWidth(etiqueta)) / 2, arriba + altoArea + 16);
            posicion++;
        }
        g2.dispose();
    }
}
