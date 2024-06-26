/* autogenerated by Processing revision 1293 on 2024-06-19 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class neuralevolution_football_v3 extends PApplet {


ArrayList<Player> player = new ArrayList();
ArrayList<Player> player2 = new ArrayList();
ArrayList<Ball> ball = new ArrayList();
ArrayList<Character> keys = new ArrayList();
int games = 500;
int best_picks = PApplet.parseInt(games*0.05f);
int default_playtime = PApplet.parseInt(60*10);
int playtime_left = default_playtime;
int generation = 1;
int round = 1;
int spawnfrom;
int spawnto;
int bspawnfrom;
int bspawnto;
float best_score = 0;
boolean showall = false;
PVector player1spawn;
PVector player2spawn;
PVector ballspawn;
int truegen = 1;
int check_games = 25;
float mutation_rate = 0.5f;
float mutation_amount = 1;
public void setup() {
  /* size commented out by preprocessor */;
  spawnfrom = 0;
  spawnto =height;
  bspawnfrom = 0;
  bspawnto = height;
  println("SKETCH STARTED");
  player1spawn = new PVector(20, random(spawnfrom, spawnto));
  player2spawn = new PVector(width-20, random(spawnfrom, spawnto));
  ballspawn = new PVector(width/2, random(bspawnfrom, bspawnto));
  for (int i = 0; i<games; i++) {
    ball.add(new Ball(ballspawn, i));
    player.add(new Player(player1spawn, true, i, 1));
    player2.add(new Player(player2spawn, false, i, 2));
    //player.get(i).loadAI();
    //player2.get(i).loadAI();
  }
  frameRate(1000);
}
public void draw() {
  mutation_rate = mutationRate();
  mutation_amount = mutationAmount();

  int spid = 10;
  //println(frameRate*spid);
  for (int abc = 0; abc < spid; abc++) {
    background(0);
    textAlign(LEFT, TOP);
    fill(255);
    textSize(60);
    text(truegen, 0, 0);

    if (playtime_left<=0&&round==2) {
      if (generation%check_games==0) {
        truegen++;
        Collections.sort(player, new Comparator<Player>() {
          public int compare(Player obj1, Player  obj2) {
            return Float.compare(obj2.score, obj1.score);
          }
        }
        );
        Collections.sort(player2, new Comparator<Player>() {
          public int compare(Player obj1, Player  obj2) {
            return Float.compare(obj2.score, obj1.score);
          }
        }
        );
        ArrayList<Player> both = new ArrayList();
        for (int i = 0; i<games; i++) {
          boolean p1g = true; //p1 good
          boolean p2g = true;
          for (int j = 0; j<player.get(i).score_rnd1.length; j++) {
            if (player.get(i).score_rnd1[j]<0) {
              p1g = false;
            }
          }
          for (int j = 0; j<player.get(i).score_rnd2.length; j++) {
            if (player.get(i).score_rnd2[j]<0) {
              p1g = false;
            }
          }
          for (int j = 0; j<player2.get(i).score_rnd1.length; j++) {
            if (player2.get(i).score_rnd1[j]<0) {
              p2g = false;
              break;
            }
          }
          for (int j = 0; j<player2.get(i).score_rnd2.length; j++) {
            if (player2.get(i).score_rnd2[j]<0) {
              p2g = false;
              break;
            }
          }
          //if (p1g) {
          both.add(player.get(i));
          //}
          //if (p2g) {
          both.add(player2.get(i));
          //}
        }
        Collections.sort(both, new Comparator<Player>() {
          public int compare(Player obj1, Player  obj2) {
            return Float.compare(obj2.score, obj1.score);
          }
        }
        );
        if (both.size()>0) {
          ArrayList<Player> top = new ArrayList();
          for (int i = 0; i<min(best_picks, both.size()); i++) {
            top.add(both.get(i));
          }



          String best_weights="";
          for (int j = 0; j<player.get(0).weights.length; j++) {
            best_weights+=player.get(0).weights[j];
            best_weights+='`';
          }
          String best_biases="";
          for (int j = 0; j<player.get(0).biases.length; j++) {
            best_biases+=player.get(0).biases[j];
            best_biases+='`';
          }
          String tst[] = new String[1];
          tst[0] = best_biases;
          saveStrings("biases.txt", tst);
          tst[0] = best_weights;
          saveStrings("weights.txt", tst);
          println(top.get(0).score, mutation_rate, mutation_amount);
          player = new ArrayList<>();
          ball = new ArrayList<>();
          player2 = new ArrayList<>();
          player1spawn = new PVector(20, random(spawnfrom, spawnto));
          player2spawn = new PVector(width-20, random(spawnfrom, spawnto));
          ballspawn = new PVector(width/2, random(bspawnfrom, bspawnto));
          for (int i = 0; i<games-top.size(); i++) {
            player.add(top.get((int)random(0, top.size())).clone());
            player2.add(top.get((int)random(0, top.size())).clone());
            player.get(i).a=1;
            player2.get(i).a=2;
            player.get(i).pos = new PVector(player1spawn.x, player1spawn.y);
            player2.get(i).pos = new PVector(player2spawn.x, player2spawn.y);
            player.get(i).left = true;
            player2.get(i).left = false;
            player.get(i).reset();
            player2.get(i).reset();
            player.get(i).mutate();
            player2.get(i).mutate();
            ball.add(new Ball(ballspawn, i));
            player.get(i).where = player.size()-1;
            player2.get(i).where = player2.size()-1;
          }
          for (int i = games-top.size(); i<games; i++) {
            player.add(top.get(games-i-1).clone());
            player2.add(top.get((int)random(0, top.size())).clone());
            player.get(i).a=1;
            player2.get(i).a=2;
            player.get(i).pos = new PVector(player1spawn.x, player1spawn.y);
            player2.get(i).pos = new PVector(player2spawn.x, player2spawn.y);
            player.get(i).left = true;
            player2.get(i).left = false;
            player.get(i).reset();
            player2.get(i).reset();
            ball.add(new Ball(ballspawn, i));
            player.get(i).where = i;
            player2.get(i).where = i;
          }
        } else {
          player = new ArrayList<>();
          ball = new ArrayList<>();
          player2 = new ArrayList<>();
          for (int i = 0; i<games; i++) {
            ball.add(new Ball(ballspawn, i));
            player.add(new Player(new PVector(player1spawn.x, player1spawn.y), true, i, 1));
            player2.add(new Player(new PVector(player2spawn.x, player2spawn.y), false, i, 2));
          }
        }
      } else {
        player1spawn = new PVector(20, random(spawnfrom, spawnto));
        player2spawn = new PVector(width-20, random(spawnfrom, spawnto));
        ballspawn = new PVector(width/2, random(bspawnfrom, bspawnto));
        for (int i = 0; i<games; i++) {
          player.get(i).pos = new PVector(player1spawn.x, player1spawn.y);
          player2.get(i).pos = new PVector(player2spawn.x, player2spawn.y);
          player.get(i).left = true;
          player2.get(i).left = false;
          player.get(i).reset();
          player2.get(i).reset();
          ball.set(i, new Ball(ballspawn, i));
        }
      }
      playtime_left = default_playtime;
      generation++;
      round = 1;
      return;
    } else if (playtime_left<=0) {// switch sides
      player1spawn = new PVector(20, random(spawnfrom, spawnto));
      player2spawn = new PVector(width-20, random(spawnfrom, spawnto));
      ballspawn = new PVector(width/2, random(bspawnfrom, bspawnto));
      round=2;
      for (int i = 0; i<games; i++) {
        player.get(i).pos = new PVector(player2spawn.x, player2spawn.y);
        player2.get(i).pos = new PVector(player1spawn.x, player1spawn.y);
        player.get(i).a=1;
        player2.get(i).a=2;
        player.get(i).left = false;
        player2.get(i).left = true;
        player.get(i).reset();
          player2.get(i).reset();
        ball.set(i, new Ball(ballspawn, i));
      }
      playtime_left = default_playtime;
    }
    for (int i = 0; i<games; i++) {
      player.get(i).guess();
      player2.get(i).guess();
      player.get(i).update();
      player2.get(i).update();
      ball.get(i).update();
      if (i>=games-best_picks/10||showall) {
        player.get(i).show();
        player2.get(i).show();
        ball.get(i).show();
      }
    }
    playtime_left--;
  }
}
public void reset() {
  for (int i = 0; i<games; i++) {
    ball.add(new Ball(ballspawn, i));
    player.add(new Player(player1spawn, true, i, 1));
    player2.add(new Player(player2spawn, false, i, 2));
  }
}
public float sign(float a) {
  return 1 / (1+exp(-a));
}
public void keyPressed() {
  if (key==' ') {
    showall=!showall;
  }
  key=0;
}
public void keyReleased() {
  for (int i = 0; i<keys.size(); i++) {
    if (keys.get(i)==key) {
      keys.remove(i);
    }
  }
}
public float mutationRate() {
  return 0.4f;
}

public float mutationAmount() {
  return 0.2f;
}
class Ball{
  PVector pos;
  PVector vel=new PVector(0,0);
  int where;
  int r = 50;
  Ball(PVector pos,int where){
    this.where = where;
    this.pos = new PVector(pos.x,pos.y);
  }
  public void update(){
    if(dist(pos.x,pos.y,player.get(where).pos.x,player.get(where).pos.y)<r/2+10){
      vel.add(PVector.mult(PVector.sub(pos,player.get(where).pos).normalize(),player.get(where).speed));
    }
    if(dist(pos.x,pos.y,player2.get(where).pos.x,player2.get(where).pos.y)<r/2+10){
      vel.add(PVector.mult(PVector.sub(pos,player2.get(where).pos).normalize(),player2.get(where).speed));
    }
    pos.add(vel);
    vel.mult(0.95f);
    if(pos.y>height-r/2){
      vel.y*=-1;
      pos.y=height-r/2;
    }else if(pos.y<r/2){
      vel.y*=-1;
      pos.y=r/2;
    }
    
    if(pos.y>height/4&&pos.y<height/4*3){
      //goal
    }else{
      if(pos.x<r/2){
        vel.x*=-1;
        pos.x=r/2;
      }else if(pos.x>width-r/2){
        vel.x*=-1;
        pos.x=width-r/2;
      } 
    }
  }
  public void show(){
    fill(255);
    ellipseMode(CENTER);
    ellipse(pos.x,pos.y,r,r);
  }
}
class Player {
  PVector pos;
  int c;
  PVector vel = new PVector(0, 0);
  float speed = 4;
  int dash_frames_left = 0;
  int dash_cooldown = 0;
  int l1 = 10;
  int l2 = 2;
  int l3 = 5;
  float weights [] = new float[l1*l2+l2*l3];
  float inputs [] = new float [l1];
  float neurons [] = new float [l2+l3];
  float biases [] = new float [l2+l3];
  boolean left;
  float threshold = 0.7f;
  float score = 0;
  float [] score_rnd1 = new float[check_games];
  float [] score_rnd2 = new float[check_games];
  int where;
  int a;
  int frames_near_ball = 0;
  int frames_goal = 0;
  public void reset() {
    speed = 2;
    frames_near_ball = 0;
    frames_goal=0;
    vel = new PVector(0, 0);
  }
  Player(PVector pos, boolean left, int where, int a) {
    this.a=a;
    this.where = where;
    this.left=left;
    for (int i = 0; i<biases.length; i++) {
      biases[i] = random(-1, 1);
    }
    for (int i = 0; i<weights.length; i++) {
      weights[i] = random(-1, 1);
    }
    if (a==1) {
      c=color(0, 0, 255);
    } else {
      c=color(255, 0, 0);
    }
    this.pos = new PVector(pos.x, pos.y);
  }
  public Player clone() {
    Player tst = new Player(pos, left, where, a);
    arrayCopy(weights, tst.weights);
    arrayCopy(biases, tst.biases);
    return tst;
  }
  public void mutate() {
    for (int i = 0; i<weights.length; i++) {
      if (random(1)<=mutation_rate) {
        if (i==0) {
          weights[i]+=random(-mutation_amount, mutation_amount);
        }
      }
    }
    for (int i = 0; i<biases.length; i++) {
      if (random(1)<=mutation_rate) {
        biases[i]+=random(-mutation_amount, mutation_amount);
      }
    }
  }
  public void guess() {
    if(dist(pos.x,pos.y,ball.get(where).pos.x,ball.get(where).pos.y)<=100)frames_near_ball++;
    if(!left){
      if(pos.x<width/3)frames_goal++;
    }else{
      if(pos.x>width/3*2)frames_goal++;
    }
    inputs[0] = map(pos.x, 0, width, -1, 1);
    inputs[1] = map(pos.y, 0, height, -1, 1);
    inputs[2]=0;
    inputs[3]=0;
    inputs[4] = map(ball.get(where).pos.x, 0, width, -1, 1);
    inputs[5] = map(ball.get(where).pos.y, 0, height, -1, 1);
    inputs[6] = map(ball.get(where).vel.x, -10, 10, -1, 1);
    inputs[7] = map(ball.get(where).vel.y, -10, 10, -1, 1);
    inputs[8] = left?1:0;
    inputs[9] = (dash_cooldown<=0)?1:0;
    for (int i = 0; i<neurons.length; i++) {
      neurons[i] = 0;
    }
    for (int i = 0; i<l1; i++) {
      for (int j = 0; j<l2; j++) {
        neurons[j] += weights[i*(l2)+j]*inputs[i];
      }
    }
    for (int i = 0; i<l2; i++) {
      neurons[i] += biases[i];
      //neurons[i] = sign(neurons[i]);
    }
    for (int i = 0; i < l2; i++) {
      for (int j = l2; j<l2+l3; j++) {
        neurons[j] += weights[l1*l2+i*l3+(j-l2)]*neurons[i];
      }
    }
    for (int i = l2; i<l2+l3; i++) {
      neurons[i] += biases[i];
      neurons[i] = sign(neurons[i]);
    }
    if (neurons[l2]>threshold) {
      up();
    }
    if (neurons[l2+2]>threshold) {
      down();
    }
    if (neurons[l2+2]>threshold) {
      left();
    }
    if (neurons[l2+3]>threshold) {
      right();
    }
    if (neurons[l2+4]>threshold) {
      dash();
    }
  }
  public void update() {
    if (a==1) {
      c=color(0, 0, 255);
    } else {
      c=color(255, 0, 0);
    }
    if (left) {
      float tscore = 0;
      if(ball.get(where).pos.x<=0)tscore+=500;
      if(ball.get(where).pos.x>=width)tscore-=100;
      tscore+=map(ball.get(where).pos.x,0,width,-50,50);
      if(tscore>500){
        println("A");
      }
      tscore+=frames_near_ball*0.1f;
      tscore+=frames_goal*0.1f;
      
      if (round==1) {
        score_rnd1[generation%check_games] = tscore;
      } else {
        score_rnd2[generation%check_games] = tscore;
      }
    } else {
      float tscore = 0;
      if(ball.get(where).pos.x<=0)tscore-=100;
      if(ball.get(where).pos.x>=width)tscore+=500;
      tscore+=map(ball.get(where).pos.x,0,width,50,-50);
      if(tscore>500){
        println("B");
      }
      tscore+=frames_near_ball*0.1f;
      tscore+=frames_goal*0.1f;
      
      if(round==1){
        score_rnd1[generation%check_games] = tscore;
      }else{
        score_rnd2[generation%check_games] = tscore;
      }
    }
    score = 0;
    for (int i = 0; i<check_games; i++) {
      score += score_rnd1[i]+score_rnd2[i];
    }
    score /= check_games*2;
    vel.normalize();
    vel.mult(speed);
    pos.add(vel);
    if (pos.x<0) {
      pos.x=0;
    }
    if (pos.x>width) {
      pos.x=width;
    }
    if (pos.y<0) {
      pos.y=0;
    }
    if (pos.y>height) {
      pos.y=height;
    }
    if (dash_frames_left<=0) {
      speed = 2;
    }
    dash_frames_left--;
    dash_cooldown--;
    vel = new PVector(0, 0);
  }
  public void show() {
    fill(c);
    ellipseMode(CENTER);
    ellipse(pos.x, pos.y, 20, 20);
    //if(where!=games-1)return;
    //if(left){
    //  int x = 150;
    //  for(int i = 0;i<l1;i++){
    //    for(int j = 0;j<l2;j++){
    //      if(weights[i*l2+j]<0){
    //        stroke(0,0,255);
    //        strokeWeight(map(weights[i*l2+j],0,-1,0,2));
    //      }else{
    //        stroke(255,0,0);
    //        strokeWeight(map(weights[i*l2+j],0,1,0,2));
    //      }
    //      line(x+0+5,i*20+5,x+50+5,j*20+5);
    //    }
    //  }
    //  for(int i = 0;i<l2;i++){
    //    for(int j = l2;j<l2+l3;j++){
    //      if(weights[l1*l2+i*l3+(j-l2)]<0){
    //        stroke(0,0,255);
    //        strokeWeight(map(weights[l1*l2+i*l3+(j-l2)],0,-1,0,2));
    //      }else{
    //        stroke(255,0,0);
    //        strokeWeight(map(weights[l1*l2+i*l3+(j-l2)],0,1,0,2));
    //      }
    //      line(x+50+5,i*20+5,x+100+5,(j-l2)*20+5);
    //    }
    //  }
    //  noStroke();
    //  ellipseMode(CORNER);
    //  for(int i = 0;i<l1;i++){
    //    fill(map(inputs[i],0,1,0,255));
    //    ellipse(x+0,i*20,10,10);
    //  }
    //  for(int i = 0;i<l2;i++){
    //    fill(map(neurons[i],0,1,0,255));
    //    ellipse(x+50,i*20,10,10);
    //  }
    //  for(int i = l2;i<l2+l3;i++){
    //    fill(map(neurons[i],0,1,0,255));
    //    ellipse(x+100,(i-l2)*20,10,10);
    //  }

    //  ellipseMode(CENTER);
    //}else{
    //  int x = 1000;
    //  for(int i = 0;i<10;i++){
    //    for(int j = 0;j<5;j++){
    //      if(weights[i*5+j]<0){
    //        stroke(0,0,255);
    //        strokeWeight(map(weights[i*5+j],0,-1,0,2));
    //      }else{
    //        stroke(255,0,0);
    //        strokeWeight(map(weights[i*5+j],0,1,0,2));
    //      }
    //      line(x+0+5,i*20+5,x+50+5,j*20+5);
    //    }
    //  }
    //  for(int i = 0;i<5;i++){
    //    for(int j = 5;j<10;j++){
    //      if(weights[50+i*5+(j-5)]<0){
    //        stroke(0,0,255);
    //        strokeWeight(map(weights[50+i*5+(j-5)],0,-1,0,2));
    //      }else{
    //        stroke(255,0,0);
    //        strokeWeight(map(weights[50+i*5+(j-5)],0,1,0,2));
    //      }
    //      line(x+50+5,i*20+5,x+100+5,(j-5)*20+5);
    //    }
    //  }
    //  noStroke();
    //  ellipseMode(CORNER);
    //  for(int i = 0;i<10;i++){
    //    fill(map(inputs[i],0,1,0,255));
    //    ellipse(x+0,i*20,10,10);
    //  }
    //  for(int i = 0;i<5;i++){
    //    fill(map(neurons[i],0,1,0,255));
    //    ellipse(x+50,i*20,10,10);
    //  }
    //  for(int i = 5;i<10;i++){
    //    fill(map(neurons[i],0,1,0,255));
    //    ellipse(x+100,(i-5)*20,10,10);
    //  }

    //  ellipseMode(CENTER);
    //}
  }
  public void dash() {
    if (dash_cooldown<=0) {
      speed = 10;
      dash_cooldown = 5*60; //5 secs
      dash_frames_left = 20;
    }
  }
  public void up() {
    vel.add(new PVector(0, -1));
  }
  public void down() {
    vel.add(new PVector(0, 1));
  }
  public void left() {
    vel.add(new PVector(-1, 0));
  }
  public void right() {
    vel.add(new PVector(1, 0));
  }
  public void loadAI() {
    String[] tst = loadStrings("data/weights.txt");
    String[] tst2 = loadStrings("data/biases.txt");
    String t = tst[0];
    String t2 = tst2[0];
    String [] a = t.split("`");
    for (int i = 0; i<a.length; i++) {
      weights[i] = Float.parseFloat(a[i]);
    }
    String [] a2 = t2.split("`");
    for (int i = 0; i<a2.length; i++) {
      biases[i] = Float.parseFloat(a2[i]);
    }
  }
}


  public void settings() { size(1200, 600, P2D); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "neuralevolution_football_v3" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
