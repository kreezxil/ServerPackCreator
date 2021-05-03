package de.griefed.serverpackcreator.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Hey, Griefed here. I tried to add a tiled background image to the frame which holds the JTabbedPane, but after serveral
 * failed attempts, I gave up and almost threw the idea out of the window. I wanted to set the background to a tiled image,
 * because simply setting a colour seemed too boring, and I needed <em>something</em> in the background so the banner
 * icon would be more clear to the eye. So, I activated my Google-Fu and encountered this holy grail of tiling images
 * for Swing.<br>
 * Links:<br>
 * <a href="https://tips4java.wordpress.com/2008/10/12/background-panel/">Background Panel by Rob Camick from October 12, 2008</a><br>
 * <a href="http://www.camick.com/java/source/BackgroundPanel.java">BackgroundPanel.java</a><br>
 * Seriously, give this man an award, because this class is a <strong>BEAST</strong>.<p>
 * Rob, if you somehow ever get wind of your class being used here: Thank you, thank you, thank you, thank you, thank you
 * so very much! You seriously made my day here.<br>
 * Rob, you rule.
 */
public class BackgroundPanel extends JPanel {
    public static final int SCALED = 0;
    public static final int TILED = 1;
    public static final int ACTUAL = 2;

    private Paint painter;

    private Image image;

    private int style = SCALED;

    private float alignmentX = 0.5f;
    private float alignmentY = 0.5f;

    private boolean isTransparentAdd = true;

    /**
     * <strong>Constructor</strong><br>
     * Set image as the background with the SCALED style.
     * @param image Pass an image to the constructor to be used in the new JPanel.
     */
    public BackgroundPanel(Image image) {
        this(image, SCALED);
    }

    /**
     * <strong>Constructor</strong><br>
     * Set image as the background with the specified style.
     * @param image Pass an image to the constructor to be used in the new JPanel.
     * @param style The style with which the image should be painted. See {@link #SCALED}, {@link #TILED}, {@link #ACTUAL}
     */
    public BackgroundPanel(Image image, int style) {
        setImage( image );
        setStyle( style );
        setLayout( new BorderLayout() );
    }

    /**
     * <strong>Constructor</strong><br>
     * Set image as the background with the specified style and alignment.
     * @param image Pass an image to the constructor to be used in the new JPanel.
     * @param style The style with which the image should be painted. See {@link #SCALED}, {@link #TILED}, {@link #ACTUAL}
     * @param alignmentX Alignment along the x-axis.
     * @param alignmentY Alignment along the y axis.
     */
    public BackgroundPanel(Image image, int style, float alignmentX, float alignmentY) {
        setImage( image );
        setStyle( style );
        setImageAlignmentX( alignmentX );
        setImageAlignmentY( alignmentY );
        setLayout( new BorderLayout() );
    }

    /**
     * <strong>Constructor</strong><br>
     * Use the Paint interface to paint a background.
     * @param painter Pass a painter to be used as the background in the new JPanel.
     */
    public BackgroundPanel(Paint painter) {
        setPaint( painter );
        setLayout( new BorderLayout() );
    }

    /**
     * Setter for the image used as the background.
     * @param image Image to be set as the background.
     */
    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    /**
     * Setter the style used to paint the background image.
     * @param style Sets the style with which the image should be painted.
     */
    public void setStyle(int style) {
        this.style = style;
        repaint();
    }

    /**
     * Setter for the Paint object used to paint the background.
     * @param painter Sets the painter with which the background should be painted.
     */
    public void setPaint(Paint painter) {
        this.painter = painter;
        repaint();
    }

    /**
     * Setter for the horizontal alignment of the image when using ACTUAL style.
     * @param alignmentX Sets the alignment along the x-axis.
     */
    public void setImageAlignmentX(float alignmentX) {
        this.alignmentX = alignmentX > 1.0f ? 1.0f : alignmentX < 0.0f ? 0.0f : alignmentX;
        repaint();
    }

    /**
     * Setter for the horizontal alignment of the image when using ACTUAL style.
     * @param alignmentY Sets the alignment along the y-axis.
     */
    public void setImageAlignmentY(float alignmentY) {
        this.alignmentY = alignmentY > 1.0f ? 1.0f : alignmentY < 0.0f ? 0.0f : alignmentY;
        repaint();
    }

    /**
     * Override method so we can make the component transparent.
     * @param component JComponent to add to the panel.
     */
    public void add(JComponent component) {
        add(component, null);
    }

    /**
     * Override to provide a preferred size equal to the image size.
     * @return Dimension. Returns the dimension of the passed image.
     */
    @Override
    public Dimension getPreferredSize() {
        if (image == null)
            return super.getPreferredSize();
        else
            return new Dimension(image.getWidth(null), image.getHeight(null));
    }

    /**
     * Override method so we can make the component transparent.
     * @param component JComponent to add to the panel.
     * @param constraints Contraints wich which the panel should be added.
     */
    public void add(JComponent component, Object constraints) {
        if (isTransparentAdd)
        {
            makeComponentTransparent(component);
        }

        super.add(component, constraints);
    }

    /**
     * Controls whether components added to this panel should automatically
     * be made transparent. That is, setOpaque(false) will be invoked.
     * The default is set to true.
     * @param isTransparentAdd Whether to automatically make components transparent.
     */
    public void setTransparentAdd(boolean isTransparentAdd) {
        this.isTransparentAdd = isTransparentAdd;
    }

    /**
     * Try to make the component transparent.
     * For components that use renderers, like JTable, you will also need to
     * change the renderer to be transparent. An easy way to do this it to
     * set the background of the table to a Color using an alpha value of 0.
     * @param component The component to make transparent.
     */
    private void makeComponentTransparent(JComponent component) {
        component.setOpaque( false );

        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane)component;
            JViewport viewport = scrollPane.getViewport();
            viewport.setOpaque( false );
            Component c = viewport.getView();

            if (c instanceof JComponent) {
                ((JComponent)c).setOpaque( false );
            }
        }
    }

    /**
     * Add custom painting.
     * @param g Received from parent.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //  Invoke the painter for the background

        if (painter != null) {
            Dimension d = getSize();
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(painter);
            g2.fill( new Rectangle(0, 0, d.width, d.height) );
        }

        //  Draw the image

        if (image == null ) return;

        switch (style) {
            case SCALED :
                drawScaled(g);
                break;

            case TILED  :
                drawTiled(g);
                break;

            case ACTUAL :
                drawActual(g);
                break;

            default:
                drawScaled(g);
        }
    }

    /**
     * Custom painting code for drawing a SCALED image as the background.
     * @param g Received from parent.
     */
    private void drawScaled(Graphics g) {
        Dimension d = getSize();
        g.drawImage(image, 0, 0, d.width, d.height, null);
    }

    /**
     * Custom painting code for drawing TILED images as the background.
     * @param g Received from parent.
     */
    private void drawTiled(Graphics g) {
        Dimension d = getSize();
        int width = image.getWidth( null );
        int height = image.getHeight( null );

        for (int x = 0; x < d.width; x += width) {
            for (int y = 0; y < d.height; y += height) {
                g.drawImage( image, x, y, null, null );
            }
        }
    }

    /**
     * Custom painting code for drawing the ACTUAL image as the background.
     * The image is positioned in the panel based on the horizontal and
     * vertical alignments specified.
     * @param g Received from parent.
     */
    private void drawActual(Graphics g) {
        Dimension d = getSize();
        Insets insets = getInsets();
        int width = d.width - insets.left - insets.right;
        int height = d.height - insets.top - insets.left;
        float x = (width - image.getWidth(null)) * alignmentX;
        float y = (height - image.getHeight(null)) * alignmentY;
        g.drawImage(image, (int)x + insets.left, (int)y + insets.top, this);
    }
}