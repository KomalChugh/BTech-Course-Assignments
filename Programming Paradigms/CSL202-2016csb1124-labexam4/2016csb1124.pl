edge(1,2).
edge(2,3).
edge(3,4).
edge(4,5).
edge(3,1).
edge(4,1).

isdiff(P,Q) :- Q is P-1,Q>0.

ispath(X,Y) :- edge(X,Y),write(X),write('\n').
ispath(X,Y) :-  edge(X,Z) ,write(X),write('\n'), ispath(Z,Y) .

path(X,Y,P) :- edge(X,Y) , P=1.
path(X,Y,P) :-  isdiff(P,Q) , edge(X,Z) ,write(Z) , write('\n'), path(Z,Y,Q) .

cycle(X) :- edge(X,Y) ,ispath(Y,X).
