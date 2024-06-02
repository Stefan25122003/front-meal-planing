#include <iostream>
#include <cstring>

using namespace std;    

int main() {
    char s[10];
    int i;
    strcpy(s,"stilou");
    cout<<s+4<<' '; 
    for(i=0;i<4;i++)
        s[i]=s[0]+(i-1)*(1-i%2)+3*(2*i/3-1)*(i%2);
    s[4]='\0';
    cout<<s;
}
