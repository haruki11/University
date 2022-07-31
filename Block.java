/*
 このブロック崩しゲームでは、最初に５つの項目「ブロックの幅」、「ブロックの高さ」、「バーの長さ」、「バーのスピード」、「ボールのスピード」の数値を範囲内で入力します。
 その後「Start」もしくは「Draw」のボタンを押します。「Start」ボタンを押すとゲームが始まり、「Draw」ボタンを押すと自分で描画するブロックを選択することができます。
ゲームを始めてブロックを崩すと4種類のアイテムが出現します。
一つ目は青の正方形のオブジェクトです。これを取るとバーに当たったときボールが止まり任意のタイミングでマウスをクリックしてボールを打ち出せます。
二つ目は赤の正方形のオブジェクトです。これを取るとボールがブロックで反射しなくなり貫通するようになります。
三つ目は緑の正方形のオブジェクトです。これを取るとボールが少し大きくなります。
四つ目はピンクの正方形のオブジェクトです。これを取ると緑とは反対にボールが少し小さくなります。
ブロックを全て崩すとゲームクリア。ボールが落ちてしまったらゲームオーバーとなります。

 */


import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.lang.model.util.Elements;
import javax.swing.*;
import javax.swing.text.AttributeSet.ColorAttribute;

public class Block extends JFrame{
    final int windowWidth = 800;
    final int windowHeight = 700;

    public static void main(String[] args){
        new Block();
    }

    public Block(){
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
        int mySpeed, myX, myY, tempMyX, myWidth, myHeight, myColorIndex, target;
        int mouseX, mouseY;
        Dimension dimOfPanel;
        Timer timer;
        int[] blockX = new int[1000];
        int[] blockY = new int[1000];
        boolean[] blockAlive = new boolean[1000];
        boolean[] blockClicked = new boolean[1000];
        int blockBroken = -1;
        int colorNum = 7;
        int[] color1 = {0, 0, 255, 255, 255, 0, 255};
        int[] color2 = {0, 255, 0, 255, 0, 255, 255};
        int[] color3 = {255, 0, 0, 0, 255, 255, 255};
        int blockStartX = 0;
        int blockStartY = 0;
        int blockWidth = 30, blockHeight = 10, blockNum = 0, drewBlockNum = 10000;//30 10
        double blockTotalRate = 0.3;
        int count_height;
        int gap = 5;
        boolean flag = true, ballStop = true, blueBallStop = false, redBall = false, blueBall = false, pinkBall = false, greenBall = false, redBallPenetrate = false;
        int milliSecond = 10;
        int ballSpeed, ballSpeedTemp, ballX, ballDirecX, ballY, ballDirecY, ballWidth, ballHeight, ballColorIndex;
        int blueBallSpeed, blueBallX, blueBallY, blueBallStayPosition, redBallSpeed, redBallX, redBallY, colorBallDiameter = 15, pinkBallX, pinkBallY, pinkBallSpeed, greenBallX, greenBallY, greenBallSpeed;
        int left, right, top, bottom;
        int status = 0, part = -1;
        JLabel labelBlockWidth, labelBlockHeight, labelMyWidth, labelMySpeed, labelBallSpeed, labelDraw;
        JTextField textBlockWidth, textBlockHeight, textMyWidth, textMySpeed, textBallSpeed;
        JButton button, drawButton, startButton;
        Random rand = new Random();

        public MyJPanel(){
            /*最初の画面 */
            setLayout(null);
            setBackground(Color.black);
            addMouseListener(this);
            addMouseMotionListener(this);

            labelBlockWidth = new JLabel("ブロックの幅 (20 ~ 120)");
            labelBlockWidth.setForeground(Color.white);
            labelBlockWidth.setBounds(250, 10, 150, 15);
            textBlockWidth = new JTextField("60", 3);
            textBlockWidth.setBounds(430, 10, 40, 15);

            labelBlockHeight = new JLabel("ブロックの高さ (10 ~ 50)");
            labelBlockHeight.setForeground(Color.white);
            labelBlockHeight.setBounds(250, 55, 150, 15);
            textBlockHeight = new JTextField("30", 2);
            textBlockHeight.setBounds(430, 55, 40, 15);

            labelMyWidth = new JLabel("バーの長さ (70 ~ 150)");
            labelMyWidth.setForeground(Color.white);
            labelMyWidth.setBounds(250, 95, 150, 15);
            textMyWidth = new JTextField("100", 3);
            textMyWidth.setBounds(430, 95, 40, 15);

            labelMySpeed = new JLabel("バーのスピード (4 ~ 15)");
            labelMySpeed.setForeground(Color.white);
            labelMySpeed.setBounds(250, 135, 150, 15);
            textMySpeed = new JTextField("8", 2);
            textMySpeed.setBounds(430, 135, 40, 15);

            labelBallSpeed = new JLabel("ボールのスピード (2 ~ 10)");
            labelBallSpeed.setForeground(Color.white);
            labelBallSpeed.setBounds(250, 175, 150, 15);
            textBallSpeed = new JTextField("4", 2);
            textBallSpeed.setBounds(430, 175, 40, 15);

            button = new JButton("Start");
            button.setBounds(300, 300, 100, 30);
            button.addActionListener(this);

            labelDraw = new JLabel("ブロックを選択");
            labelDraw.setForeground(Color.white);
            labelDraw.setBounds(300, 460, 150, 15);
            drawButton = new JButton("Draw");
            drawButton.setBounds(300, 500, 100, 30);
            drawButton.addActionListener(this);


            add(labelBlockWidth);
            add(textBlockWidth);
            add(labelBlockHeight);
            add(textBlockHeight);
            add(labelMyWidth);
            add(textMyWidth);
            add(labelMySpeed);
            add(textMySpeed);
            add(labelBallSpeed);
            add(textBallSpeed);
            add(button);
            add(labelDraw);
            add(drawButton);
        }

        public void startApp(){
            /*ゲームスタート */
            timer = new Timer(milliSecond, this);
            timer.start();
            
            myHeight = 15;
            myX = windowWidth / 2 - myWidth / 2;
            myY = windowHeight - 100;
            myColorIndex = colorNum - 1;
            ballX = myX+myWidth/4*3;
            ballDirecX = -1;
            ballY = myY-myHeight-2;
            ballDirecY = 1;
            ballWidth = 15;
            ballSpeed = ballSpeedTemp * milliSecond / 10;
            ballColorIndex = colorNum -1;

            blockBroken = 0;

        }

        public void initBlock(){    
            /*ブロックの情報作成 */
            j = 0;
            i = 0;
            while(blockStartY + blockHeight < dimOfPanel.height * blockTotalRate){
                while(blockStartX + blockWidth < dimOfPanel.width){
                    blockX[i] = blockStartX;
                    blockY[i] = blockStartY;
                    blockAlive[i] = true;
                    blockClicked[i] = false;
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
            /*ゲームオーバー */
            g.setColor(Color.red);
            Font f = new Font("Serif", Font.BOLD, 40);
            g.setFont(f);
            g.drawString("Game Over", dimOfPanel.width / 2 - 100, dimOfPanel.height / 2);
        }

        public void gameClear(Graphics g){
            /*ゲームクリア */
            g.setColor(Color.blue);
            Font f = new Font("Serif", Font.BOLD, 40);
            g.setFont(f);
            g.drawString("Game Clear", dimOfPanel.width / 2 - 100, dimOfPanel.height / 2);
            status = 1;
        }

        public void paintComponent(Graphics g){
            dimOfPanel = getSize();
            
            super.paintComponent(g);

            if(part == 2){

                /*ゲームオーバーの判定 */
                if(status == -1){
                    gameOver(g);
                }
    
                /*ゲームクリアの判定 */
                if(blockNum == blockBroken || drewBlockNum == blockBroken){
                    gameClear(g);
                }

                /*アイテム取得 */
                if(blueBall){
                    drawBlueBall(g);
                }
    
                if(redBall){
                    drawRedBall(g);
                }
    
                if(pinkBall){
                    drawPinkBall(g);
                }
    
                if(greenBall){
                    drawGreenBall(g);
                }
    
                drawBlock(g);
                drawMyStick(g);
                drawBall(g);
            } else if (part == 1){
                /*描画モードへ移行 */
                initBlock();
                drawRec(g);
                part = 3;
            } else if(part == 3){
                /*描画モード */
                drawRec(g);
            }
        }

        public void drawMyStick(Graphics g){
            /*バーを描画 */
            if (myX + myWidth / 4 < mouseX && mouseX < myX+myWidth / 4 * 3){

            } else if(0 < myX && mouseX < myX + myWidth / 2){
                myX -= mySpeed;
            } else if (myX + myWidth / 2 < mouseX && myX + myWidth < dimOfPanel.width){
                myX += mySpeed;
            }
            g.setColor(new Color(color1[myColorIndex], color2[myColorIndex], color3[myColorIndex]));
            g.fillRect(myX, 600, myWidth, 10);
        }

        public void drawBlock(Graphics g){
            /*ブロックを描画 */
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
                if(blockAlive[i] && blockClicked[i]){
                    g.fillRect(blockX[i], blockY[i], blockWidth, blockHeight);
                }
                temp = blockY[i];
            }
            colorIndex++;   
        }

        public void drawBall(Graphics g){
            

            //ボールとバーの接触
            if(!ballStop){
                if(ballY+ballHeight >= myY-myHeight-ballSpeed && ballY+ballHeight <= myY-myHeight && myX < ballX + ballWidth / 2 && ballX + ballWidth / 2 < myX + myWidth && blueBallStop == false){
                    ballDirecY *= -1;
                    ballColorIndex = myColorIndex;
                    if(ballX + ballWidth / 2 <= myX + myWidth / 4){
                        ballDirecX = -2;
                    } else if(ballX + ballWidth / 2 <= myX + myWidth / 2){
                        ballDirecX = -1;
                    } else if(ballX + ballWidth / 2 <= myX + myWidth / 4 * 3){
                        ballDirecX = 1;
                    } else {
                        ballDirecX = 2;
                    }

                    if(myColorIndex == 0){
                        ballDirecY *= -1;
                        blueBallStop = true;
                    }

                    if(myColorIndex == 2){
                        redBallPenetrate = true;
                    } else {
                        redBallPenetrate = false;
                    }

                    
                    myColorIndex = colorNum - 1;

                    blueBallStayPosition = ballX - myX;
                }
    
                //ボールと壁の接触
                if(ballX <= 0){
                    ballDirecX *= -1;
                } else if(dimOfPanel.width <= ballX + ballWidth){
                    ballDirecX *= -1;
                } else if(ballY <= 0){
                    ballDirecY *= -1;
                }
    
                //ボールが落ちた
                if(ballY >= dimOfPanel.height){
                    if(status != 1){
                        status = -1;
                    }
                }
    
                //ボールとブロックの接触
                for(int i=0; i<blockNum; i++){
                    if(blockAlive[i] && blockClicked[i]){
                        if(ballY + ballWidth > blockY[i] && ballY < blockY[i] + blockHeight && ballX < blockX[i] + blockWidth && ballX + ballWidth > blockX[i]){
                            blockAlive[i] = false;
                            blockBroken++;
    
                            top = Math.abs(ballY + ballWidth - blockY[i]);
                            bottom = Math.abs(ballY - (blockY[i] + blockHeight));
                            right = Math.abs(ballX - (blockX[i] + blockWidth));
                            left = Math.abs(ballX + ballWidth - blockX[i]);
    
                            if(redBallPenetrate == false){
                                if((top < right && top < left) || (bottom < right && bottom <left)){
                                    ballDirecY *= -1;
                                } else {
                                    ballDirecX *= -1;
                                }
                            }

                            int num = rand.nextInt(4);
                            if(num == 0 && blueBall == false){
                                blueBall = true;
                                blueBallX = blockX[i] + blockWidth / 2;
                                blueBallY = blockY[i];
                                num = rand.nextInt(3) + 2;
                                blueBallSpeed = num;
                            } else if(num == 1 && redBall == false){
                                redBall = true;
                                redBallX = blockX[i] + blockWidth / 2;
                                redBallY = blockY[i];
                                num = rand.nextInt(3) + 2;
                                redBallSpeed = num;
                            } else if(num == 2 && pinkBall == false){
                                pinkBall = true;
                                pinkBallX = blockX[i] + blockWidth / 2;
                                pinkBallY = blockY[i];
                                num = rand.nextInt(3) + 2;
                                pinkBallSpeed = num;
                            } else if(num == 3 && greenBall == false){
                                greenBall = true;
                                greenBallX = blockX[i] + blockWidth / 2;
                                greenBallY = blockY[i];
                                num = rand.nextInt(3) + 2;
                                greenBallSpeed = num;
                            }
                        }
                    }
                }

                /*青のアイテムを取っているか */
                if(blueBallStop){
                    ballX = myX+blueBallStayPosition;
                } else {
                    ballY += ballDirecY * ballSpeed;
                    ballX += ballDirecX * ballSpeed;
                }

            } else {
                ballX = myX+myWidth/4*3;
            }
            g.setColor(new Color(color1[ballColorIndex], color2[ballColorIndex], color3[ballColorIndex]));
            g.fillOval(ballX, ballY, ballWidth, ballWidth);
        }

        public void drawBlueBall(Graphics g){
            /*青いアイテムの描画 */
            blueBallY += blueBallSpeed;
            if(blueBallY+colorBallDiameter >= myY-myHeight-blueBallSpeed && blueBallY+colorBallDiameter <= myY-myHeight && myX < blueBallX + colorBallDiameter / 2 && blueBallX + colorBallDiameter / 2 < myX + myWidth){
                blueBall = false;
                myColorIndex = 0;
            }

            if(blueBallY >= dimOfPanel.height){
                blueBall = false;
            }
            g.setColor(Color.blue);
            g.fillRect(blueBallX, blueBallY, colorBallDiameter, colorBallDiameter);
        }

        public void drawRedBall(Graphics g){
            /*赤いアイテムの描画 */
            redBallY += redBallSpeed;
            if(redBallY+colorBallDiameter >= myY-myHeight-redBallSpeed && redBallY+colorBallDiameter <= myY-myHeight && myX < redBallX + colorBallDiameter / 2 && redBallX + colorBallDiameter / 2 < myX + myWidth){
                redBall = false;
                myColorIndex = 2;
            }

            if(redBallY >= dimOfPanel.height){
                redBall = false;
            }

            g.setColor(Color.red);
            g.fillRect(redBallX, redBallY, colorBallDiameter, colorBallDiameter);
        }

        public void drawPinkBall(Graphics g){
            /*ピンクのアイテムの描画 */
            pinkBallY += pinkBallSpeed;
            if(pinkBallY+colorBallDiameter >= myY-myHeight-pinkBallSpeed && pinkBallY+colorBallDiameter <= myY-myHeight && myX < pinkBallX + colorBallDiameter / 2 && pinkBallX + colorBallDiameter / 2 < myX + myWidth){
                pinkBall = false;
                if(ballWidth >= 6){
                    ballWidth -= 5;
                }
            }

            if(pinkBallY >= dimOfPanel.height){
                pinkBall = false;
            }

            g.setColor(Color.magenta);
            g.fillRect(pinkBallX, pinkBallY, colorBallDiameter, colorBallDiameter);
        }

        public void drawGreenBall(Graphics g){
            /*緑のアイテムの描画 */
            greenBallY += greenBallSpeed;
            if(greenBallY+colorBallDiameter >= myY-myHeight-greenBallSpeed && greenBallY+colorBallDiameter <= myY-myHeight && myX < greenBallX + colorBallDiameter / 2 && greenBallX + colorBallDiameter / 2 < myX + myWidth){
                greenBall = false;
                if(ballWidth <= 40){
                    ballWidth += 5;
                }
            }

            if(greenBallY >= dimOfPanel.height){
                greenBall = false;
            }

            g.setColor(Color.green);
            g.fillRect(greenBallX, greenBallY, colorBallDiameter, colorBallDiameter);
        }

        public void drawRec(Graphics g){
            /*描画モードの時、ブロックを描画 */
            for(int i=0; i<blockNum; i++){
                g.setColor(Color.red);
                if(blockClicked[i]){
                    g.fillRect(blockX[i], blockY[i], blockWidth, blockHeight);
                } else {
                    g.drawRect(blockX[i], blockY[i], blockWidth, blockHeight);
                }
            }
        }

        public void actionPerformed(ActionEvent e){
            /*ボタンの検知 */
            if(e.getSource() == button || e.getSource() == drawButton){
                /*入力された数値を変数に格納 */
                String temp = textBlockWidth.getText();
                int num = Integer.valueOf(temp);
                if(num < 20){
                    blockWidth = 20;
                } else if (120 < num){
                    blockWidth = 120;
                } else {
                    blockWidth = num;
                }

                temp = textBlockHeight.getText();
                num = Integer.valueOf(temp);
                if(num < 10){
                    blockHeight = 10;
                } else if (50 < num){
                    blockHeight = 50;
                } else {
                    blockHeight = num;
                }

                temp = textMyWidth.getText();
                num = Integer.valueOf(temp);
                if(num < 70){
                    myWidth = 70;
                } else if (150 < num){
                    myWidth = 150;
                } else {
                    myWidth = num;
                }

                temp = textMySpeed.getText();
                num = Integer.valueOf(temp);
                if(num < 4){
                    mySpeed = 4;
                } else if (15 < num){
                    mySpeed = 15;
                } else {
                    mySpeed = num;
                }

                temp = textBallSpeed.getText();
                num = Integer.valueOf(temp);
                if(num < 2){
                    ballSpeedTemp = 2;
                } else if (10 < num){
                    ballSpeedTemp = 10;
                } else {
                    ballSpeedTemp = num;
                }

                remove(labelBlockWidth);
                remove(textBlockWidth);
                remove(labelBlockHeight);
                remove(textBlockHeight);
                remove(labelMyWidth);
                remove(textMyWidth);
                remove(labelMySpeed);
                remove(textMySpeed);
                remove(labelBallSpeed);
                remove(textBallSpeed);
                remove(button);
                remove(labelDraw);
                remove(drawButton);
            }
            if(e.getSource() == button){
                initBlock();
                part = 2;
                for(int i=0; i<blockNum; i++){
                    blockClicked[i] = true;
                }
                startApp();
            } else if(e.getSource() == drawButton){
                /*描画モード */
                startButton = new JButton("Start");
                startButton.setBounds(300, 300, 100, 30);
                startButton.addActionListener(this);

                add(startButton);
                part = 1;
            } else if(e.getSource() == startButton){
                /*描画モードからゲームをスタート */
                boolean f = false;
                int num = 0;
                for(int i=0; i<blockNum; i++){
                    if(blockClicked[i]){
                        f = true;
                        num++;
                    }
                }
                if(f){
                    remove(startButton);
                    drewBlockNum = num;
                    part = 2;
                    startApp();
                }
            }
            repaint();
        }

        public void mouseClicked(MouseEvent e){

        }

        public void mousePressed(MouseEvent e){
            /*マウスが押されたとき */
            if(part == 3){
                mouseX = e.getX();
                mouseY = e.getY();
                for(int i=0; i<blockNum; i++){
                    if(mouseY > blockY[i] && mouseY < blockY[i] + blockHeight && mouseX < blockX[i] + blockWidth && mouseX + ballWidth > blockX[i]){
                        if(blockClicked[i]){
                            blockClicked[i] = false;
                        } else {
                            blockClicked[i] = true;
                        }
                        repaint();
                    }
                }
            }

            if(blueBallStop){
                blueBallStop = false;
            }
            ballStop = false;
        }

        public void mouseReleased(MouseEvent e){

        }

        public void mouseExited(MouseEvent e){

        }

        public void mouseEntered(MouseEvent e){

        }

        public void mouseMoved(MouseEvent e){
            /*マウスが動いたとき */
            mouseX = e.getX();
        }

        public void mouseDragged(MouseEvent e){

        }
    }
}