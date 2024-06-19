class Ball{
  PVector pos;
  PVector vel=new PVector(0,0);
  int where;
  int r = 50;
  Ball(PVector pos,int where){
    this.where = where;
    this.pos = new PVector(pos.x,pos.y);
  }
  void update(){
    if(dist(pos.x,pos.y,player.get(where).pos.x,player.get(where).pos.y)<r/2+10){
      vel.add(PVector.mult(PVector.sub(pos,player.get(where).pos).normalize(),player.get(where).speed));
    }
    if(dist(pos.x,pos.y,player2.get(where).pos.x,player2.get(where).pos.y)<r/2+10){
      vel.add(PVector.mult(PVector.sub(pos,player2.get(where).pos).normalize(),player2.get(where).speed));
    }
    pos.add(vel);
    vel.mult(0.95);
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
  void show(){
    fill(255);
    ellipseMode(CENTER);
    ellipse(pos.x,pos.y,r,r);
  }
}
