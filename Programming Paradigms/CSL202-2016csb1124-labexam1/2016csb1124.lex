/* definitions */
field	[^:|]+":"
end	[^:|]+"|"
%{
    int array[1000];    // for storing keys
    int color=0xaaa,diff=0xaf;
    int flag=0,l=0,key=0;
    int flag1=0,i,last=0; // last is for the last field of input.txt which is without : or |
%}
%%
{field}		{ 
		  if(flag==0){
		  flag1=0; key=0;
		  int k=yyleng-2,j=1;
		  while(k>=0){
		    key=key+((yytext[k]-'0')*j);
		    j=j*10;
		    k--;}
		  array[l]=key;
		  for(i=0;i<l;i++)
		  {
		     if(array[i]==key)
		     {printf("<tr bgcolor=\"#%x\">",color+i*diff);
		     flag1=1; break;}
		  }
		  if(flag1==0)
		     printf("<tr bgcolor=\"#%x\">",color+l*diff);
		  printf("<td>%d</td><td>%d</td>",++l,key);
		  flag=1;
		  }
		  
		  else{
		  printf("<td>");
		  int m=0;
		  while(m<=yyleng-2)
		  { printf("%c",yytext[m]);
		    m++;  }
		  printf("</td>");
		  }
		}
{end}		{
		  printf("<td>");
		  int z=0;
		  while(z<=yyleng-2)
		  { printf("%c",yytext[z]);
		    z++;  }
		  printf("</td></tr>");
		  flag=0;
		}
.		{
		   if(last==0)
		   {  printf("<td>");
		      last=1;
		   }
		   ECHO;
		}
%%
int main(int argc,char* argv[]){
    printf("<html><body><table>");
    yyin=fopen(argv[1],"r");
    yylex();
    printf("</td></tr></table></body></html>");
    fclose(yyin);
}
