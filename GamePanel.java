import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Time;


public class GamePanel extends JPanel implements Runnable{
    private static final int PWIDTH = 500;
    private static final int PHEIGHT = 400;//game window
    private static final int NO_DELAYS_PER_YIELD = 16;
    private static final int MAX_FRAME_SKIPS = 5;
    private static final int period = 17; //60FPS  1000/60 = 16.77777

    private Thread animator; //for animation
    private volatile boolean running = false; //stops animation
    private volatile boolean gameOver = false; //for game termination
    private volatile boolean isPaused = false;

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
        if(!isPaused && !gameOver){
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
    private void pausedGame(){isPaused = true;}

    public void run(){
        long beforeTime,afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;
        beforeTime = System.nanoTime();

        running = true;
        while(running){
            gameUpdate();
            gameUpdate();
            gameRender();
            //repaint();

            //active rendering
            paintScreen();

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;//period :iteration cost time;

            if(sleepTime > 0){
                try{
                    Thread.sleep(sleepTime/10000000L);//nano -> ms
                }catch (InterruptedException ex){}
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            }
            else{ //sleep < 0; frame took longer than the period
                excess -= sleepTime;
                overSleepTime = 0L;

                if(++noDelays >= NO_DELAYS_PER_YIELD){
                    Thread.yield(); //give another thread to run
                    noDelays = 0;
                }
            }
            beforeTime = System.nanoTime();

            int skips = 0;
            while((excess >= period)&&(skips <= MAX_FRAME_SKIPS)){
                excess -= period;
                gameUpdate();
                skips++;
            }
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
        try {
            g = this.getGraphics();
            if((g != null) && (dbImage != null))
                g.drawImage(dbImage,0,0,null);
            Toolkit.getDefaultToolkit().sync();
        } catch (Exception e) {
            System.out.println("Graphics context error: " +e);
        }
    }//end

    private void gameOverMessage(Graphics g){
        g.drawString(msg,x,y);
    }


}