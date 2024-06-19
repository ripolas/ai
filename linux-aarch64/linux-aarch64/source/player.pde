class Player {
  PVector pos;
  color c;
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
  float threshold = 0.7;
  float score = 0;
  float [] score_rnd1 = new float[check_games];
  float [] score_rnd2 = new float[check_games];
  int where;
  int a;
  int frames_near_ball = 0;
  int frames_goal = 0;
  void reset() {
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
  Player clone() {
    Player tst = new Player(pos, left, where, a);
    arrayCopy(weights, tst.weights);
    arrayCopy(biases, tst.biases);
    return tst;
  }
  void mutate() {
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
  void guess() {
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
  void update() {
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
      tscore+=frames_near_ball*0.1;
      tscore+=frames_goal*0.1;
      
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
      tscore+=frames_near_ball*0.1;
      tscore+=frames_goal*0.1;
      
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
  void show() {
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
  void dash() {
    if (dash_cooldown<=0) {
      speed = 10;
      dash_cooldown = 5*60; //5 secs
      dash_frames_left = 20;
    }
  }
  void up() {
    vel.add(new PVector(0, -1));
  }
  void down() {
    vel.add(new PVector(0, 1));
  }
  void left() {
    vel.add(new PVector(-1, 0));
  }
  void right() {
    vel.add(new PVector(1, 0));
  }
  void loadAI() {
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
