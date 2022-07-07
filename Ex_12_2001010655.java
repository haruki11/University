/*
大学の授業の自由制作で作っているブロック崩しゲームです。７月末完成予定です。
*/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Ex_12_2001010655 extends JFrame{
    final int windowWidth = 800;
    final int windowHeight = 700;

    public static void main(String[] args){
        new Ex_12_2001010655();
    }

    public Ex_12_2001010655(){
        Dimension dimOfScreen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(dimOfScreen.width/2 - windowWidth/2,
                    dimOfScreen.height/2 - windowHeight/2,
                    windowWidth, windowHeight);
        
        setResizable(false);
        setTitle("Software Development II");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        MyJPanel panel = new MyJPanel();
        Container c  = getContentPane();
        c.add(panel);
        setVisible(true);
    }

    public class MyJPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
        int i, j;
        int mySpeed, myX, myY, tempMyX, myWidth, myHeight;
        int mouseX;
        Dimension dimOfPanel;
        Timer timer;
        int[] blockX = new int[1000];
        int[] blockY = new int[1000];
        boolean[] blockAlive = new boolean[1000];
        int blockBroken;
        int colorNum = 6;
        int[] color1 = {0, 0, 255, 255, 255, 0};
        int[] color2 = {0, 255, 0, 255, 0, 255};
        int[] color3 = {255, 0, 0, 0, 255, 255};
        int blockStartX = 0;
        int blockStartY = 0;
        int blockWidth = 30, blockHeight = 10;//30 10
        double blockTotalRate = 0.3;
        int blockNum, count_height;
        int gap = 5;
        boolean flag = true, ballStop = true;
        int milliSecond = 10;
        int ballSpeed, ballX, ballDirecX, ballY, ballDirecY, ballWidth, ballHeight;
        int left, right, top, bottom;
        int status = 0;

        public MyJPanel(){
            setLayout(null);
            setBackground(Color.black);
            addMouseListener(this);
            addMouseMotionListener(this);

            timer = new Timer(milliSecond, this);
            timer.start();


            //initBlock();

            myWidth = 100;
            myHeight = 10;
            myX = windowWidth / 2 - myWidth / 2;
            myY = windowHeight - 100;
            mySpeed = 8 * milliSecond / 10;
            ballX = myX+myWidth/4*3;
            ballDirecX = -1;
            ballY = myY-myHeight-2;
            ballDirecY = 1;
            ballWidth = 10;
            ballSpeed = 4 * milliSecond / 10;
        }

        public void initBlock(){    
            
            j = 0;
            i = 0;
            while(blockStartY + blockHeight < dimOfPanel.height * blockTotalRate){
                while(blockStartX + blockWidth < dimOfPanel.width){
                    blockX[i] = blockStartX;
                    blockY[i] = blockStartY;
                    blockAlive[i] = true;
                    blockStartX += gap + blockWidth;
                    i++;
                }
                blockStartX = 0;
                blockStartY += gap + blockHeight;
                j++;
            }
            blockNum = i;
            count_height = j;
            flag = false;
        }

        public void gameOver(Graphics g){
            g.setColor(Color.red);
            Font f = new Font("Serif", Font.BOLD, 40);
            g.setFont(f);
            g.drawString("Game Over", dimOfPanel.width / 2 - 100, dimOfPanel.height / 2);
        }

        public void gameClear(Graphics g){
            g.setColor(Color.blue);
            Font f = new Font("Serif", Font.BOLD, 40);
            g.setFont(f);
            g.drawString("Game Clear", dimOfPanel.width / 2 - 100, dimOfPanel.height / 2);
            status = 1;
        }

        public void paintComponent(Graphics g){
            dimOfPanel = getSize();
            
            super.paintComponent(g);
            if(flag){
                initBlock();
            }

            if(status == -1){
                gameOver(g);
            }

            if(blockNum == blockBroken){
                gameClear(g);
            }
            drawBlock(g);
            drawMyStick(g);
            drawBall(g);
        }

        public void drawMyStick(Graphics g){
            
            if (myX + myWidth / 4 < mouseX && mouseX < myX+myWidth / 4 * 3){

            } else if(0 < myX && mouseX < myX + myWidth / 2){
                myX -= mySpeed;
            } else if (myX + myWidth / 2 < mouseX && myX + myWidth < dimOfPanel.width){
                myX += mySpeed;
            }
            g.setColor(Color.green);
            g.fillRect(myX, 600, myWidth, 10);
        }

        public void drawBlock(Graphics g){
            int colorIndex = 0;
            int temp = -1;

            for(int i=0; i<blockNum; i++){
                if(temp != blockY[i]){
                    if(colorIndex == colorNum){
                        colorIndex = 0;
                    }
                    g.setColor(new Color(color1[colorIndex], color2[colorIndex], color3[colorIndex]));
                    colorIndex++;
                }
                if(blockAlive[i]){
                    g.fillRect(blockX[i], blockY[i], blockWidth, blockHeight);
                }
                temp = blockY[i];
            }
            colorIndex++;   
        }

        public void drawBall(Graphics g){
            //ボールとバーの接触
            if(!ballStop){
                if(ballY+ballHeight >= myY-myHeight-ballSpeed && ballY+ballHeight <= myY-myHeight && myX < ballX + ballWidth / 2 && ballX + ballWidth / 2 < myX + myWidth){
                    ballDirecY *= -1;
                    if(ballX + ballWidth / 2 <= myX + myWidth / 4){
                        ballDirecX = -2;
                    } else if(ballX + ballWidth / 2 <= myX + myWidth / 2){
                        ballDirecX = -1;
                    } else if(ballX + ballWidth / 2 <= myX + myWidth / 4 * 3){
                        ballDirecX = 1;
                    } else {
                        ballDirecX = 2;
                    }
                }
    
                //ボールと壁の接触
                if(ballX <= 0){
                    ballDirecX *= -1;
                } else if(dimOfPanel.width <= ballX + ballWidth){
                    ballDirecX *= -1;
                } else if(ballY <= 0){
                    ballDirecY *= -1;
                }
    
                if(ballY >= dimOfPanel.height){
                    if(status != 1){
                        status = -1;
                    }
                }
    
                for(int i=0; i<blockNum; i++){
                    if(blockAlive[i]){
                        if(ballY + ballWidth > blockY[i] && ballY < blockY[i] + blockHeight && ballX < blockX[i] + blockWidth && ballX + ballWidth > blockX[i]){
                            blockAlive[i] = false;
                            blockBroken++;
    
                            top = Math.abs(ballY + ballWidth - blockY[i]);
                            bottom = Math.abs(ballY - (blockY[i] + blockHeight));
                            right = Math.abs(ballX - (blockX[i] + blockWidth));
                            left = Math.abs(ballX + ballWidth - blockX[i]);
    
                            if((top < right && top < left) || (bottom < right && bottom <left)){
                                ballDirecY *= -1;
                            } else {
                                ballDirecX *= -1;
                            }
                        }
                    }
                }
    
    
                ballY += ballDirecY * ballSpeed;
                ballX += ballDirecX * ballSpeed;
            } else {
                ballX = myX+myWidth/4*3;
            }
            g.setColor(Color.white);
            g.fillOval(ballX, ballY, ballWidth, ballWidth);
        }

        public void actionPerformed(ActionEvent e){
            repaint();
        }

        public void mouseClicked(MouseEvent e){

        }

        public void mousePressed(MouseEvent e){
            ballStop = false;
        }

        public void mouseReleased(MouseEvent e){

        }

        public void mouseExited(MouseEvent e){

        }

        public void mouseEntered(MouseEvent e){

        }

        public void mouseMoved(MouseEvent e){
            mouseX = e.getX();

        }

        public void mouseDragged(MouseEvent e){

        }
    }
}
