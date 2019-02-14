oid(200).
oid(201).
oname(200,'Ubuntu').
oname(201,'Fedore').
olibs(200,['lxml', 'gcc', 'foo', 'bar']).
olibs(201,['lib_a', 'lib_b', 'lib_image', 'bar']).
mid(120).
mid(121).
mtype(120,'Physical').
mtype(121,'Virtual').
mos(120,200).
mos(121,201).
mram(120,16384.0).
mram(121,4096.0).
mdisk(120,6144.0).
mdisk(121,256.0).
mcpu(120,16).
mcpu(121,2).
sname(300,'MySQL Server').
sname(300,'Apache Web Server').
sname(300,'ImageProcessing Server').
sram(300,'MySQL Server',512.0).
sram(300,'Apache Web Server',512.0).
sram(300,'ImageProcessing Server',2.0).
sdisk(300,'MySQL Server',4.0).
sdisk(300,'Apache Web Server',1.0).
sdisk(300,'ImageProcessing Server',100.0).
scpu(300,'MySQL Server',2).
scpu(300,'Apache Web Server',2).
scpu(300,'ImageProcessing Server',8).
sos(300,'MySQL Server',[200, 201]).
sos(300,'Apache Web Server',[200]).
sos(300,'ImageProcessing Server',[200]).
slibs(300,'MySQL Server',['lxml', 'gcc', 'foo', 'bar']).
slibs(300,'Apache Web Server',['lib_a', 'gcc', 'lib_b', 'bar']).
slibs(300,'ImageProcessing Server',['keras', 'gcc', 'lib_image', 'bar']).
memberchk(X, [Y|Ys]) :- X = Y ; memberchk(X, Ys).
subset([], _).
subset([X|Tail], Y):- memberchk(X, Y),subset(Tail, Y).
isBiggerThan(X,Y) :-
    (  X >= Y
       -> true
       ;  false
    ).
checkmachine(Id,MId):- slibs(Id,Name,Slist),sos(Id,Name,OSId),memberchk(OS,OSId),olibs(OS,Olist),subset(Slist,Olist),mos(MId,OS),write('Machine Id : '),write(MId),write('
'),mtype(MId,MType),write('Machine Type : '),write(MType),nl,write('OS : '),write(OS),nl,mram(MId,MRAM),write('RAM : '),write(MRAM),write(' MB'),nl,mdisk(MId,MDisk),write('DISK : '),write(MDisk),write(' GB'),nl,mcpu(MId,MCPU),write('CPU : '),write(MCPU),write(' cores'),nl,sram(Id,Name,SRAM),sdisk(Id,Name,SDisk),scpu(Id,Name,SCPU),isBiggerThan(MRAM,SRAM),isBiggerThan(MDisk,SDisk),isBiggerThan(MCPU,SCPU).