package com.chunyu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class GamePanel extends JPanel implements Runnable{
    private static final int PWIDTH = 500;
    private static final int PHEIGHT = 400;//game window

    private Thread animator; //for animation
    private volatile boolean running = false; //stops animation

    private volatile boolean gameOver = false; //for game termination

    public GamePanel(){
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        //adding User Interaction
        setFocusable(true);
        requestFocus(); //Jpanel now receicves key events
        readyForTermination();

        //create game component(構成要素)
        //...

        //listen for mouse presses
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                testPress(e.getX(),e.getY());
            }
        });
    }

    private void readyForTermination(){
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if((keyCode == KeyEvent.VK_ESCAPE) ||
                        (keyCode == KeyEvent.VK_Q) ||
                        (keyCode == KeyEvent.VK_END)||
                        ((keyCode == KeyEvent.VK_C) && e.isControlDown()) ){
                    running = false;
                }
            }
        });
    }

    private void testPress(int x, int y){
        //is (x,y) important to Game?
        if(!gameOver){
            //do Something
        }
    }



    private void startGame(){
        //initialise and start
        if(animator == null || !running){
            animator = new Thread(this);
            animator.start();
        }
    }
    public void addNotify(){
        /*Wait for the Jpanel to be added to the Jframe/JApplet before starting. */
        super.addNotify();
        startGame();
    }
    private void stopGame(){ running = false;}

    public void run(){
        running = true;
        while(running){
            gameUpdate();
            gameRender();
            //repaint();

            //active rendering
            paintScreen();
            try{
                Thread.sleep(20);
            }catch (InterruptedException ex){}
        }
        System.exit(0);
    }


    private void gameUpdate(){
        if(!gameOver){
            //do Something
        }
    }

    // global variable for odd-screen rendering
    private Graphics dbg;
    private Image dbImage = null;
    private void gameRender(){
        if(dbImage == null){
            dbImage = createImage(PWIDTH, PHEIGHT);
            if(dbImage == null){
                System.out.println("dbImage is null");
                return;
            }
            else{
                dbg = dbImage.getGraphics();
            }

            //clear the background
            dbg.setColor(Color.white);
            dbg.fillRect(0,0,PWIDTH, PHEIGHT);

            //draw game elements
            // ...

            if(gameOver)
                gameOverMessage(dbg);
        }
    }

    //after rendering ,called by repaint()
    //because we switch to active rendering this member is incorporate into paintSceen()
    /*public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(dbImage != null){
            g.drawImage(dbImage,0,0,null);
        }
    }*/

    private void paintScreen(){
        Graphics g;

    }

    /*private void gameOverMessage(Graphics g){
        g.drawString(msg,x,y);
    }*/
}