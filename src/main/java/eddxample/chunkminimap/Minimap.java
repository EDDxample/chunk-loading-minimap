package eddxample.chunkminimap;

import eddxample.chunkminimap.mixin.ChunkAccessorMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;


public class Minimap {
    private static HashMap<Long, Integer> chunk_list = new HashMap<>();
    private static JFrame minimap;
    private static DimensionType dim = DimensionType.OVERWORLD;

    public static void init() {
        System.setProperty("java.awt.headless", "false");
        javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() { initMinimap(); } });
    }
    public static void initMinimap() {
        minimap = new JFrame("Chunk Minimap");
        minimap.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        minimap.setSize(600, 600);
        MinimapPanel mp = new MinimapPanel();
        minimap.getContentPane().addMouseListener(mp);
        minimap.getContentPane().addMouseMotionListener(mp);
        minimap.getContentPane().addMouseWheelListener(mp);
        minimap.add(mp);
        minimap.setVisible(true);
        System.setProperty("java.awt.headless", "true");
    }
    public static void closeMinimap() {
        if (minimap != null) minimap.dispatchEvent(new WindowEvent(minimap, WindowEvent.WINDOW_CLOSING));
    }

    public static int getChunk(long chunkID) {
//        synchronized ((IChunker) (Object) ((ServerChunkManager) MinecraftClient.getInstance().getServer().getWorld(DimensionType.OVERWORLD).getChunkManager()).threadedAnvilChunkStorage) {
            try {
                return status2int(((IChunker) (Object) ((ServerChunkManager) MinecraftClient.getInstance().getServer().getWorld(DimensionType.OVERWORLD).getChunkManager()).threadedAnvilChunkStorage).getIt(chunkID).getCompletedStatus());
            } catch (Exception e) {
                return 0xA0A0A0;
            }
//        }
    }
    public static void addChunk(long chunkID, ChunkStatus status) {
        synchronized (chunk_list) {
            if (minimap == null) return;
            if (status == null) chunk_list.remove(chunkID);
            else chunk_list.put(chunkID, status2int(status));
            minimap.repaint();
        }
    }

    public static void update() { if (minimap != null) minimap.repaint(); }

    public static int status2int(ChunkStatus status) {
        int new_status = 0;
        if (status == null) return 0xA0A0A0;
        switch (status.getName()) {
            case "empty":            new_status = 0xA0A0A0; break; //
            case "structure_starts": new_status = 0xFFFF00; break; //
            case "biomes":           new_status = 0xFFA000; break;
            case "noise":            new_status = 0xFF00FF; break;
            case "surface":          new_status = 0xFFA0FF; break;
            case "carvers":          new_status = 0xA0FFA0; break;
            case "liquid_carvers":   new_status = 0x00FFFF; break; //
            case "features":         new_status = 0x00A000; break; //
            case "light":            new_status = 0xA0A000; break;
            case "spawn":            new_status = 0x00FFA0; break;
            case "heightmaps":       new_status = 0xF0F0F0; break;
            case "full":             new_status = 0x00FF00; break; //
        }
        return new_status;
    }

    public static interface IChunker { ChunkHolder getIt(long l); }
}


class MinimapPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener {

    static int origenX, origenY, clickX, clickY, chunkSize = 40, centerChunkX, centerChunkY;
    static boolean shouldDrag = false;


    public static long asLong(int x, int z)
    {
        return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
    }

    static Color getColor(int x, int z) {
        Color c = new Color(Minimap.getChunk(asLong(x, z)));
        return (x + z) % 2 == 0 ? c : getDarker(c);
    }
    static Color getDarker(Color c) {
        int lower = 60;
        int r = Math.max(0, c.getRed()   - lower);
        int g = Math.max(0, c.getGreen() - lower);
        int b = Math.max(0, c.getBlue()  - lower);
        return new Color(r,g,b);
    }
    static int[] getRadius(Rectangle bounds) {
        int midX = bounds.width/2,
            midY = bounds.height/2;
        centerChunkX = midX - ((midX - origenX) % chunkSize);
        centerChunkY = midY - ((midY - origenY) % chunkSize);
        int radiusX = (int) (Math.floor((double)(centerChunkX + (chunkSize*3))/chunkSize))*chunkSize;
        int radiusY = (int) (Math.floor((double)(centerChunkY + (chunkSize*4))/chunkSize))*chunkSize;
        return new int[] {radiusX, radiusY};
    }

    public void paint(Graphics g1) {
        super.paint(g1);
        Graphics2D g = (Graphics2D) g1;
        Rectangle bounds = g.getClipBounds();

        int[] radius = getRadius(bounds);

        //DRAW CHUNKS
        for (int j = centerChunkY - radius[1]; j < centerChunkY + radius[1]; j += chunkSize)
        {
            for (int i = centerChunkX - radius[0]; i < centerChunkX + radius[0]; i += chunkSize)
            {
                //ChunkPos
                int x = (i - origenX) / chunkSize;
                int y = (j - origenY) / chunkSize;


                Color col = getColor(x, y);

                g.setColor(col);
                g.fillRect(i, j, chunkSize, chunkSize);

                g.setColor(Color.black);

                String text = x+", "+y;

                int textWidth = g.getFontMetrics().stringWidth(text);

                if (textWidth < chunkSize && chunkSize > 10) g.drawString(text, i + chunkSize/2 - textWidth/2, j + 5 + chunkSize/2);

            }
        }


        //0,0 LINES
        g.setColor(new Color(0xFF0000));
        g.drawLine(0, origenY, bounds.width, origenY);
        g.drawLine(0, origenY+1, bounds.width, origenY+1);
        g.setColor(new Color(0x0000FF));
        g.drawLine(origenX, 0, origenX, bounds.height);
        g.drawLine(origenX+1, 0, origenX+1, bounds.height);
    }


    //EVENTS
    public void mousePressed(MouseEvent e) {
        shouldDrag = false;
        if (e.getButton() == 1)
        {
            shouldDrag = true;
            clickX = e.getX();
            clickY = e.getY();
        }
        else if (e.getButton() == 3)
        {
            int x = e.getX() - (e.getX() - origenX)%chunkSize;
            int y = e.getY() - (e.getY() - origenY)%chunkSize;
            System.out.println(x+" , "+y);
        }
    }
    public void mouseDragged(MouseEvent e) {
        if (shouldDrag)
        {
            origenX += e.getX() - clickX;
            origenY += e.getY() - clickY;
            clickX = e.getX();
            clickY = e.getY();
            repaint();
        }
    }
    public void mouseWheelMoved(MouseWheelEvent e) {
        double i = -e.getPreciseWheelRotation();
        int save = chunkSize;
        if (i < 0) chunkSize += chunkSize > 6 ? i : 0;
        if (i > 0) chunkSize += chunkSize < 60 ? i : 0;

        if (chunkSize != save)
        {
            origenX += (i > 0 ? -1 : 1)*((e.getX() - origenX)/chunkSize + 1);
            origenY += (i > 0 ? -1 : 1)*((e.getY() - origenY)/chunkSize + 1);
            repaint();
        }
    }


    public void actionPerformed(ActionEvent e){}
    public void mouseClicked   (MouseEvent e) {}
    public void mouseReleased  (MouseEvent e) {}
    public void mouseEntered   (MouseEvent e) {}
    public void mouseExited    (MouseEvent e) {}
    public void mouseMoved     (MouseEvent e) {}
}


