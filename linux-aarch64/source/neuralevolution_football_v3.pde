import java.util.*;
ArrayList<Player> player = new ArrayList();
ArrayList<Player> player2 = new ArrayList();
ArrayList<Ball> ball = new ArrayList();
ArrayList<Character> keys = new ArrayList();
int games = 500;
int best_picks = int(games*0.05);
int default_playtime = int(60*10);
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
float mutation_rate = 0.5;
float mutation_amount = 1;
void setup() {
  size(1200, 600, P2D);
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
void draw() {
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
void reset() {
  for (int i = 0; i<games; i++) {
    ball.add(new Ball(ballspawn, i));
    player.add(new Player(player1spawn, true, i, 1));
    player2.add(new Player(player2spawn, false, i, 2));
  }
}
float sign(float a) {
  return 1 / (1+exp(-a));
}
void keyPressed() {
  if (key==' ') {
    showall=!showall;
  }
  key=0;
}
void keyReleased() {
  for (int i = 0; i<keys.size(); i++) {
    if (keys.get(i)==key) {
      keys.remove(i);
    }
  }
}
float mutationRate() {
  return 0.4;
}

float mutationAmount() {
  return 0.2;
}
