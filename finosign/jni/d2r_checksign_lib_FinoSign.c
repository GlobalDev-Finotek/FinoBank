

#include "d2r_checksign_lib_FinoSign.h"
#include <stdio.h>
#include <string.h>

#include <android/log.h>


#define TIME_LOCK 0

//#include "resource.h"
//#include "finosign.h"
//#include <stdio.h>
#include <stdlib.h>				 
#include <math.h>
#include <time.h>





// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------

// 서명을 3개로 분할한다. 
int Divide_Sign(unsigned char *sign_file,
				unsigned char *file1,
				unsigned char *file2,
				unsigned char *file3)
{
	unsigned char *p;
	int len,i;
	int s1,s2;
	FILE *fp;

	fp = fopen(sign_file,"rb");
	if (fp==0) return (0);
	fseek(fp,0,SEEK_END);
	len = ftell(fp);
	fseek(fp,0,SEEK_SET);
	p = malloc(len);
	fread(p,len,1,fp);

	for (i=0;i<len;i++)	p[i] = 255-p[i];

	s1 = len/3;
	s2 = len*2/3;

	fp = fopen(file1,"w+b");
	if (fp==0) return (0);
	for (i=0;i<s1;i++)	fwrite(&p[i],1,1,fp);
	fclose(fp);

	fp = fopen(file2,"w+b");
	if (fp==0) return (0);
	for (i=s1;i<s2;i++)	fwrite(&p[i],1,1,fp);
	fclose(fp);

	fp = fopen(file3,"w+b");
	if (fp==0) return (0);
	for (i=s2;i<len;i++) fwrite(&p[i],1,1,fp);
	fclose(fp);

	free(p);

	return (1);
}

// 3개로 분할된 서명정보를 1개로 합친다. 
int Merge_Sign(unsigned char *file1,
			   unsigned char *file2,
			   unsigned char *file3,
			   unsigned char *sign_file)
{
	unsigned char *p;
	FILE *fp,*wfp;
	int i,k,len;

	wfp = fopen(sign_file,"w+b");
	if (wfp==0) return (0);

	for (k=0;k<3;k++)
	{
		if (k==0) fp = fopen(file1,"rb");
		if (k==1) fp = fopen(file2,"rb");
		if (k==2) fp = fopen(file3,"rb");
		if (fp==0) return (0);
		fseek(fp,0,SEEK_END);
		len = ftell(fp);
		fseek(fp,0,SEEK_SET);
		p = malloc(len);
		fread(p,len,1,fp);
		for (i=0;i<len;i++) p[i] = 255-p[i];
		fclose(fp);
		fwrite(p,len,1,wfp);
		free(p);
	}

	fclose(wfp);
	return (1);
}

// ----------------------------------------------------
// 병렬처리 옵션
// ----------------------------------------------------

#define SOPT1 3      // 손떼기 획수의 오차 범위 
#define SOPT2 50     // 서명의 가로크기의 오차 비율범위(%) 
#define SOPT3 50     // 서명의 세로크기의 오차 비율범위(%) 
#define SOPT4 75	 // 가로,세로의 비율 허용 범위(%)
#define SOPT5 50	 // 서명 전체모양 비교에서 오차범위를 규정(%) 30
#define SOPT6 25	 // 전체 서명 속도 오차범위(%)  70
#define SOPT7 40	 // 전체 서명 흐름의 오차범위(%) 
#define SOPT8 500	 // 각각의 오브젝트의 서명 흐름의 오차범위(%) 
#define SOPT9 15	 // 각각의 오브젝트 사이의 서명 속도의 오차범위(%) 18
#define SOPT10 200   // 각각의 오브젝트의 시작좌표의 오차범위(pixel) 150
#define SOPT11 200   // 각각의 오브젝트의 끝좌표의 오차범위(pixel) 150
#define SOPT12 250   // 각각의 오브젝트의 가로크기의 오차범위(%)
#define SOPT13 300   // 각각의 오브젝트의 세로크기의 오차범위(%)
#define SOPT14 250   // 각각의 오브젝트의 가로세로 비율의 오차범위(%)
#define SOPT15 1000    // 각각의 오브젝트의 비트맵모양의 오차범위(%)

// 리턴값 : 2개의 서명간의 흐름 유사도
//p1, p2 서명데이터파일을 메모리에 읽은 값. 
//x좌표,y좌표,시간,x좌표,y좌표,시간 - - - - - - - - - - - - -  손을 떼었을 경우 라인변경
//x좌표,y좌표,시간,x좌표,y좌표,시간 - - - - - - - - - - - - -  손을 떼었을 경우 라인변경
//x좌표,y좌표,시간,x좌표,y좌표,시간 - - - - - - - - - - - - -  손을 떼었을 경우 라인변경

//int xs1 = 0;

int Compare_FinoSign(unsigned char *p1,unsigned char *p2) 
{
	int i,j,k,min,speed;
	int ret,dist,pass;
	int xs1,xs2,ys1,ys2,ratio1,ratio2;
	int sp1,sp2,start,end;

	//
	// 타임락을 체크한다. 
	//

#if TIME_LOCK
	struct tm *timeptr;
	time_t secsnow;

	timezone = 8*60*60;
	time(&secsnow);
	timeptr = localtime(&secsnow);
	if (timeptr->tm_year==116 || timeptr->tm_year==117)
	{
	}
	else
	{
		return (0);
	}
#endif

	// 	struct tm *timeptr;
	// 	time_t secsnow;
	// 
	// 	timezone = 8*60*60;
	// 	time(&secsnow);
	// 	timeptr = localtime(&secsnow);
	// 	if (timeptr->tm_year==116 || timeptr->tm_year==117)
	// 	{
	// 	}
	// 	else
	// 	{
	// 		return (0);
	// 	}


	//
	// 데이타를 읽어온다. 
	//
	pnum1 = Get_Pen_Data(p1,pen_data1,rect_data[0]);
	pnum2 = Get_Pen_Data(p2,pen_data2,rect_data[1]);

	// 손떼기 오차가 SOPT1이상이면 같은 서명으로 보지 않는다. 
	if (abs(pnum1-pnum2)>SOPT1)   
		return (0);

	//
	// 기본 서명 크기에 대한 정보를 구한다. 
	//

	xs1 = rect_data[0][2]-rect_data[0][0]+1;
	xs2 = rect_data[1][2]-rect_data[1][0]+1;
	ys1 = rect_data[0][3]-rect_data[0][1]+1;
	ys2 = rect_data[1][3]-rect_data[1][1]+1;
	ratio1 = xs1*100/xs2;
	ratio2 = ys1*100/ys2;

	//
	// 가로크기 50%
	//

	if (xs1>xs2)
	{
		if (xs2<xs1-xs1*SOPT2/100 || xs2>xs1+xs1*SOPT2/100)
			return (0);
	}
	else
	{
		if (xs1<xs2-xs2*SOPT2/100 || xs1>xs2+xs2*SOPT2/100)
			return (0);
	}

	//
	// 세로크기 50%
	//

	if (ys1>ys2)
	{
		if (ys2<ys1-ys1*SOPT3/100 || ys2>ys1+ys1*SOPT3/100)
			return (0);
	}
	else
	{
		if (ys1<ys2-ys2*SOPT3/100 || ys1>ys2+ys2*SOPT3/100)
			return (0);
	}

	//
	// 가로세로 비율의 범위
	//

	if (ratio1<ratio2-SOPT4 || ratio1>ratio2+SOPT4)
		return (0);

	//
	// 서명 크기를 동일한 크기로 맞추어 준다. 
	// normalize(500*250으로 맞춘다.)
	//

	Get_Normalize(pen_data1,rect_data[0]); //좌표 0, 0 기준으로 pen_data1 재배치
	Get_Normalize(pen_data2,rect_data[1]); //좌표 0, 0 기준으로 pen_data2 재배치

	// 
	// 전체 모양을 비교한다. 
	//

	{
		unsigned char **img1,**img2;
		unsigned char data1[1250],data2[1250];

		// 서명데이타를 bitmap으로 전환한다. 
		// 50X25의 작은 크기로 변환 
		img1 = Get_Image(pen_data1);
		img2 = Get_Image(pen_data2);	

		Get_Data(img1,data1);
		Get_Data(img2,data2);
		dist = distance(data1,1250,data2,1250);
		for (i=0;i<25;i++)
		{
			free(img1[i]);
			free(img2[i]);
		}
		free(img1);
		free(img2);

		// SOPT5%이상 오차가 발생하면 같은 서명으로 보지 않는다. 
		if (dist>1250*SOPT5/100)
			return (0);
	}

	//
	// 전체의 속도를 비교한다. 
	//

	// src 
	start = pen_data1[0][0][2];
	for (i=0;i<5000;i++)
	{
		if (pen_data1[pnum1-1][i][2]>-1 && pen_data1[pnum1-1][i+1][2]==-1)
		{
			end = pen_data1[pnum1-1][i][2];
			break;
		}
	}
	sp1 = end-start;

	// dst 
	start = pen_data2[0][0][2];
	for (i=0;i<5000;i++)
	{
		if (pen_data2[pnum2-1][i][2]>-1 && pen_data2[pnum2-1][i+1][2]==-1)
		{
			end = pen_data2[pnum2-1][i][2];
			break;
		}
	}
	sp2 = end-start;
	speed = sp1*SOPT6/100;
	//if (speed<50) speed = 50;
	if (sp2<sp1-speed || sp2>sp1+speed)
		return (0);

	//
	// 선의 흐름(각각의 오브젝트)
	//

	if (pnum1==pnum2)
	{
		if (Check_Segment(pnum1,pen_data1,pen_data2)==0)
			return (0);
	}
	else 
	{
		return (0);
	}
// 	else
// 		if (pnum2==pnum1-1)
// 		{
// 			pass = 0;
// 			for (i=0;i<pnum1-1;i++)		
// 			{
// 				Merge_Data1(pen_data3,pen_data1,i);
// 				if (Check_Segment(pnum2,pen_data2,pen_data3)==1)
// 				{
// 					pass = 1;
// 					break;
// 				}
// 			}
// 			if (pass==0)
// 				return (0);
// 		}
// 		else
// 			if (pnum1==pnum2-1)
// 			{
// 				pass = 0;
// 				for (i=0;i<pnum2-1;i++)		
// 				{
// 					Merge_Data1(pen_data3,pen_data2,i);
// 					if (Check_Segment(pnum1,pen_data1,pen_data3)==1)
// 					{
// 						pass = 1;
// 						break;
// 					}
// 				}
// 				if (pass==0)
// 					return (0);
// 			}
// 			else
// 				if (pnum2==pnum1-2)
// 				{
// 					pass = 0;
// 					for (i=0;i<pnum1-1;i++)	
// 					{
// 						for (j=i+2;j<pnum1-1;j++)
// 						{
// 							Merge_Data2(pen_data3,pen_data1,i,j);
// 							if (Check_Segment(pnum2,pen_data2,pen_data3)==1)
// 							{
// 								pass = 1;
// 								i = 1000;
// 								j = 1000;
// 							}
// 						}
// 					}
// 					if (pass==0)
// 						return (0);
// 				}
// 				else
// 					if (pnum1==pnum2-2)
// 					{
// 						pass = 0;
// 						for (i=0;i<pnum2-1;i++)	
// 						{
// 							for (j=i+2;j<pnum2-1;j++)
// 							{
// 								Merge_Data2(pen_data3,pen_data2,i,j);
// 								if (Check_Segment(pnum1,pen_data1,pen_data3)==1)
// 								{
// 									pass = 1;
// 									i = 10000;
// 									j = 10000;
// 								}
// 							}
// 						}
// 						if (pass==0)
// 							return (0);
// 					}
// 					else
// 						if (pnum2==pnum1-3)
// 						{
// 							pass = 0;
// 							for (i=0;i<pnum1-1;i++)	
// 							{
// 								for (j=i+2;j<pnum1-1;j++)
// 								{
// 									for (k=j+2;k<pnum1-1;k++)
// 									{
// 										Merge_Data3(pen_data3,pen_data1,i,j,k);
// 										if (Check_Segment(pnum2,pen_data2,pen_data3)==1)
// 										{
// 											pass = 1;
// 											i = 1000;
// 											j = 1000;
// 											k = 1000;
// 										}
// 									}
// 								}
// 							}
// 							if (pass==0)
// 								return (0);
// 						}
// 						else
// 							if (pnum1==pnum2-3)
// 							{
// 								pass = 0;
// 								for (i=0;i<pnum2-1;i++)	
// 								{
// 									for (j=i+2;j<pnum2-1;j++)
// 									{
// 										for (k=j+2;k<pnum2-1;k++)
// 										{
// 											Merge_Data3(pen_data3,pen_data2,i,j,k);
// 											if (Check_Segment(pnum1,pen_data1,pen_data3)==1)
// 											{
// 												pass = 1;
// 												i = 10000;
// 												j = 10000;
// 												k = 10000;
// 											}
// 										}
// 									}
// 								}
// 								if (pass==0)
// 									return (0);
// 							}

	//
	// 서명 전체의 선의 흐름
	//

	min = 100000;
	len1 = Get_Total_String(str1,pen_data1);
	len2 = Get_Total_String(str2,pen_data2);
	if (len1>len2+len2*SOPT7/100 || len2>len1+len1*SOPT7/100)
		return (0);

	dist = distance(str1,len1,str2,len2);
	ret = (10000-(dist*10000/((len1+len2))))+(int)sqrt(len1+len2)*10; //sprt 근사값 구하는 함수 (루트값)
	if (ret>=10000)	ret = 9999;
	if (ret<=0) ret = 0;
	if (ret<min) min = ret;

	return (min);
}

// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------

static int Get_Length(int data[5000][3])
{
	int i;

	for (i=0;i<5000;i++)
	{
		if (data[i][0]==-1)
			return (i);
	}
	return (0);
}

static int Merge_Data1(int data1[100][5000][3],int data2[100][5000][3],int index)
{
	int i,j,ii,jj,l1,l2;

	i = 0;
	j = 0;
	while (data2[i][0][0]!=-1)
	{
		if (i!=index)
		{
			for (ii=0;ii<5000;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}
			j++;
		}
		else
		{
			l1 = Get_Length(data2[j]);
			l2 = Get_Length(data2[j+1]);
			for (ii=0;ii<l1;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}

			for (ii=0;ii<l2;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii+l1][jj] = data2[j+1][ii][jj];
			}

			data1[i][ii+l1][0] = -1;
			data1[i][ii+l1][1] = -1;
			data1[i][ii+l1][2] = -1;
			j += 2;
		}
		i++;
	}
	return (1);
}

static int Merge_Data2(int data1[100][5000][3],int data2[100][5000][3],int index1,int index2)
{
	int i,j,ii,jj,l1,l2;

	i = 0;
	j = 0;
	while (data2[i][0][0]!=-1)
	{
		if (i!=index1 && i!=index2)
		{
			for (ii=0;ii<5000;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}
			j++;
		}
		else
		{
			l1 = Get_Length(data2[j]);
			l2 = Get_Length(data2[j+1]);
			for (ii=0;ii<l1;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}

			for (ii=0;ii<l2;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii+l1][jj] = data2[j+1][ii][jj];
			}

			data1[i][ii+l1][0] = -1;
			data1[i][ii+l1][1] = -1;
			data1[i][ii+l1][2] = -1;
			j += 2;
		}
		i++;
	}
	return (1);
}

static int Merge_Data3(int data1[100][5000][3],int data2[100][5000][3],int index1,int index2,int index3)
{
	int i,j,ii,jj,l1,l2;

	i = 0;
	j = 0;
	while (data2[i][0][0]!=-1)
	{
		if (i!=index1 && i!=index2 && i!=index3)
		{
			for (ii=0;ii<5000;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}
			j++;
		}
		else
		{
			l1 = Get_Length(data2[j]);
			l2 = Get_Length(data2[j+1]);
			for (ii=0;ii<l1;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}

			for (ii=0;ii<l2;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii+l1][jj] = data2[j+1][ii][jj];
			}

			data1[i][ii+l1][0] = -1;
			data1[i][ii+l1][1] = -1;
			data1[i][ii+l1][2] = -1;
			j += 2;
		}
		i++;
	}
	return (1);
}

static int Get_Speed(int data[5000][3])
{
	int len;

	len = Get_Length(data);
	return (data[len-1][2]-data[0][2]);
}

static int Get_Speed2(int data1[5000][3],int data2[5000][3])
{
	int len;

	len = Get_Length(data1);
	return (data2[0][2]-data1[len-1][2]);
}

static int Get_Start_Locate(int data[5000][3],int *x,int *y)
{
	*x = data[0][0];
	*y = data[0][1];
	return (1);
}

static int Get_End_Locate(int data[5000][3],int *x,int *y)
{
	int len;

	len = Get_Length(data);
	*x = data[len-1][0];
	*y = data[len-1][1];
	return (1);
}

static int Check_Segment(int num,int data1[100][5000][3],int data2[100][5000][3])
{
	int i,j,len1,len2,ret;
	int sp1,sp2;
	int x1,y1,x2,y2;

	//
	// 각각의 세그먼트간의 선의 흐름의 distance를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		len1 = Get_Object_String(str1,data1,i,i,num);
		len2 = Get_Object_String(str2,data2,i,i,num);
		if (len1>100 || len2>100)
		{
			ret = Get_Percent(str1,len1,str2,len2);
			if (ret<5000)
				return (0);

			if (len1>len2+len2*SOPT8/100 || len2>len1+len1*SOPT8/100)
				return (0);
		}
	}

	//
	// 각각의 세그먼트 속도를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		sp1 = Get_Speed(data1[i]);
		sp2 = Get_Speed(data2[i]);
		if (sp1>100 || sp2>100)
		{
			if (sp1>sp2*SOPT9/10 || sp2>sp1*SOPT9/10)
				return (0);
		}
	}

	//
	// 각각의 세그먼트사이의 속도를 계산한다.
	//

	for (i=0;i<num-1;i++)
	{
		sp1 = Get_Speed2(data1[i],data1[i+1]);
		sp2 = Get_Speed2(data2[i],data2[i+1]);
		if (sp1>100 || sp2>100)
		{
			if (sp1>sp2*SOPT9/10 || sp2>sp1*SOPT9/10)
				return (0);
		}
	}

	//
	// 시작좌표의 오차를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		Get_Start_Locate(data1[i],&x1,&y1);
		Get_Start_Locate(data2[i],&x2,&y2);
		if (abs(x1-x2)>SOPT10 || abs(y1-y2)>SOPT10)
			return (0);
	}

	//
	// 끝나는 좌표의 오차를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		Get_End_Locate(data1[i],&x1,&y1);
		Get_End_Locate(data2[i],&x2,&y2);
		if (abs(x1-x2)>SOPT11 || abs(y1-y2)>SOPT11)
			return (0);
	}
	if (num<=1)
		return (1);

	//
	// 각각의 오브젝트의 모양을 확인한다. 
	//

	for (i=0;i<num;i++)
	{
		int xs1,ys1,xs2,ys2,range1[4],range2[4];
		int ratio1,ratio2;

		Get_Image_Size(data1[i],range1);
		Get_Image_Size(data2[i],range2);
		xs1 = range1[2]-range1[0]+1;
		ys1 = range1[3]-range1[1]+1;
		xs2 = range2[2]-range2[0]+1;
		ys2 = range2[3]-range2[1]+1;

		// 각각의 가로 획의 크기를 비교한다. 
		if (xs1>30 && xs2>30)
		{
			if (xs1>xs2*SOPT12/100 || xs2>xs1*SOPT12/100)
				return (0);
		}

		// 각각의 세로 획의 크기를 비교한다. 		
		if (ys1>30 && ys2>30)
		{
			if (ys1>ys2*SOPT13/100 || ys2>ys1*SOPT13/100)
				return (0);
		}

		// 각각의 가로세로 비율을 확인한다. 
		if (xs1>30 && xs2>30 && ys1>30 && ys2>30)
		{
			ratio1 = xs1*100/ys1;
			ratio2 = xs2*100/ys2;
			if (ratio1>ratio2*SOPT14/100 || ratio2>ratio1*SOPT14/100)
				return (0);
		}

		// 최종 획의 모양
		if ((xs1>30 && ys1>15) || (xs2>30 && ys2>15))
		{
			int dist;
			unsigned char **img1,**img2;
			unsigned char dat1[1250],dat2[1250];

			img1 = Get_Image_Part(data1[i],range1);
			img2 = Get_Image_Part(data2[i],range2);	

			Get_Data(img1,dat1);
			Get_Data(img2,dat2);
			dist = distance(dat1,1250,dat2,1250);
			for (j=0;j<25;j++)
			{
				free(img1[j]);
				free(img2[j]);
			}
			free(img1);
			free(img2);

			if (dist>SOPT15)
				return (0);
		}
	}

	return (1);
}

static int Get_Image_Size(int data[5000][3],int range[4])
{
	int j,x1,y1,x2,y2;

	x1 = 1000000;
	y1 = 1000000;
	x2 = -1;
	y2 = -1;

	// 기본 좌표를 0,0으로 맞춘다. 
	j = 0;
	while (data[j][0]!=-1)
	{
		if (data[j][0]<x1) x1 = data[j][0];
		if (data[j][1]<y1) y1 = data[j][1];
		if (data[j][0]>x2) x2 = data[j][0];
		if (data[j][1]>y2) y2 = data[j][1];
		j++;
	}

	range[0] = x1;
	range[1] = y1;
	range[2] = x2;
	range[3] = y2;

	return (1);
}

static int Get_Percent(unsigned char *str1,int len1,unsigned char *str2,int len2)
{
	int dist,ret;

	if (len1==0 || len2==0)
		return (0);

	dist = distance(str1,len1,str2,len2);
	ret = (10000-(dist*10000/((len1+len2))))+(int)sqrt(len1+len2)*10;
	return (ret);
}

static int Get_Normalize(int data[100][5000][3],int rect_data[4])
{
	int i,j,xfac,yfac;
	int xs,ys;

	// 기본 좌표를 0,0으로 맞춘다. 
	i = 0;
	while (data[i][0][0]!=-1)
	{
		j = 0;
		while (data[i][j][0]!=-1)
		{
			data[i][j][0] -= rect_data[0];
			data[i][j][1] -= rect_data[1];
			j++;
		}
		i++;
	}

	// 사이즈를 500,250으로 맞추어준다. 
	xs = rect_data[2]-rect_data[0]+1;
	ys = rect_data[3]-rect_data[1]+1;
	xfac = xs*10000/500;
	yfac = ys*10000/250;

	i = 0;
	while (data[i][0][0]!=-1)
	{
		j = 0;
		while (data[i][j][0]!=-1)
		{
			data[i][j][0] = data[i][j][0]*10000/xfac;
			data[i][j][1] = data[i][j][1]*10000/yfac;
			j++;
		}
		i++;
	}
	return (1);
}

static int Get_Normalize_Part(int data[5000][3],int rect_data[4])
{
	int j,xfac,yfac;
	int xs,ys;

	// 기본 좌표를 0,0으로 맞춘다. 
	j = 0;
	while (data[j][0]!=-1)
	{
		data[j][0] -= rect_data[0];
		data[j][1] -= rect_data[1];
		j++;
	}

	// 사이즈를 500,250으로 맞추어준다. 
	xs = rect_data[2]-rect_data[0]+1;
	ys = rect_data[3]-rect_data[1]+1;
	xfac = xs*10000/500;
	yfac = ys*10000/250;

	j = 0;
	while (data[j][0]!=-1)
	{
		data[j][0] = data[j][0]*10000/xfac;
		data[j][1] = data[j][1]*10000/yfac;
		j++;
	}
	return (1);
}

static unsigned char **Get_Image_Part(int data[5000][3],int range[4])
{
	int i,j,xs,ys,buf[5000][3];
	unsigned char **img;

	for (i=0;i<5000;i++)
	{
		for (j=0;j<3;j++)
			buf[i][j] = data[i][j];
	}

	Get_Normalize_Part(buf,range);

	xs = 50;
	ys = 25;
	img = (unsigned char **)malloc(ys*sizeof(unsigned char *));
	for (i=0;i<ys;i++)
	{
		img[i] = (unsigned char *)malloc(xs);
		for (j=0;j<xs;j++)
			img[i][j] = 0;
	}

	j = 1;
	while (buf[j][0]!=-1)
	{
		Set_Image(img,buf[j-1][0],buf[j-1][1],buf[j][0],buf[j][1]);
		j++;
	}

	return (img);
}

static unsigned char **Get_Image(int data[100][5000][3])
{
	int i,j,xs,ys;
	unsigned char **img;

	xs = 50;
	ys = 25;

	img = (unsigned char **)malloc(ys*sizeof(unsigned char *));
	for (i=0;i<ys;i++)
	{
		img[i] = (unsigned char *)malloc(xs);
		for (j=0;j<xs;j++)
			img[i][j] = 0;
	}

	i = 0;
	while (data[i][0][0]!=-1)
	{
		j = 1;
		while (data[i][j][0]!=-1)
		{
			Set_Image(img,data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1]);
			j++;
		}
		i++;
	}
	return (img);
}

static int Set_Image(unsigned char **img,int xx1,int yy1,int xx2,int yy2)
{
	int x1,y1,x2,y2,xlen,ylen;
	int i,j,aa,b;

	if (xx1<0 || xx1>=500 || xx2<0 || xx2>=500)
		return (0);

	if (yy1<0 || yy1>=250 || yy2<0 || yy2>=250)
		return (0);

	xlen = abs(xx1-xx2);
	ylen = abs(yy1-yy2);
	if (xlen>ylen)
	{		
		if (xx1<=xx2)
		{
			x1 = xx1;
			y1 = yy1;
			x2 = xx2;
			y2 = yy2;
		}
		else
		{
			x1 = xx2;
			y1 = yy2;
			x2 = xx1;
			y2 = yy1;
		}

		img[y1/10][x1/10] = 1;
		img[y2/10][x2/10] = 1;
		for (j=x1+1;j<x2-1;j++)
		{
			aa = ylen*10000/xlen;
			b = aa*(j-x1-1)/10000;
			if (aa%10000>5000) b++;

			if (y1<y2) img[(y1+b)/10][j/10] = 1;
			else       img[(y1-b)/10][j/10] = 1;
		}
	}
	else
	{
		if (yy1<=yy2)
		{
			x1 = xx1;
			y1 = yy1;
			x2 = xx2;
			y2 = yy2;
		}
		else
		{
			x1 = xx2;
			y1 = yy2;
			x2 = xx1;
			y2 = yy1;
		}

		img[y1/10][x1/10] = 1;
		img[y2/10][x2/10] = 1;
		for (i=y1+1;i<y2-1;i++)
		{
			aa = xlen*10000/ylen;
			b = aa*(i-y1-1)/10000;
			if (aa%10000>5000) b++;

			if (x1<x2) img[i/10][(x1+b)/10] = 1;
			else       img[i/10][(x1-b)/10] = 1;
		}
	}
	return (1);
}

static int Get_Data(unsigned char **img,unsigned char data[1250])
{
	int i,j,k;

	k = 0;
	for (i=0;i<25;i+=2)
	{
		for (j=0;j<50;j++,k++)
		{
			if (img[i][j]==1) data[k] = 'D';
			else			  data[k] = 'B';
		}

		if (i<24)
		{
			for (j=49;j>=0;j--,k++)
			{
				if (img[i+1][j]==1) data[k] = 'D';
				else			    data[k] = 'B';
			}
		}
	}
	return (1);
}

static int Get_Total_String(unsigned char *str,int data[100][5000][3])
{
	int i,j,k,n,ret;
	unsigned char buf[10000];

	i = 0;
	n = 0;
	while (data[i][0][0]!=-1)
	{
		j = 1;
		while (data[i][j][0]!=-1)
		{
			ret = Get_String(data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1],buf,0);
			for (k=0;k<ret;k++)
			{
				str[n] = buf[k];
				n++;
			}
			j++;
		}
		i++;
		if (n>0 && data[i][0][0]!=-1)
		{
			ret = Get_String(data[i-1][j-1][0],data[i-1][j-1][1],data[i][0][0],data[i][0][1],buf,1);
			for (k=0;k<ret;k++)
			{
				str[n] = buf[k];
				n++;
			}
		}
	}

	return (n);
}

static int Get_Object_String(unsigned char *str,int data[100][5000][3],int index1,int index2,int num)
{
	int i,j,k,n,ret;
	unsigned char buf[10000];

	if (index1<0) 
		return (0);

	if (index2>num-1)
		return (0);

	n = 0;
	for (i=index1;i<=index2;i++)
	{
		if (data[i][0][0]!=-1)
		{
			j = 1;
			while (data[i][j][0]!=-1)
			{
				ret = Get_String(data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1],buf,0);
				for (k=0;k<ret;k++)
				{
					str[n] = buf[k];
					n++;
				}
				j++;
			}
		}
	}

	return (n);
}

static int Get_Object_String_Reverse(unsigned char *str,int data[100][5000][3],int index1,int index2,int num)
{
	int i,j,k,n,ret;
	unsigned char buf[10000],str1[10000];

	if (index1<0) 
		return (0);

	if (index2>num-1)
		return (0);

	n = 0;
	for (i=index1;i<=index2;i++)
	{
		if (data[i][0][0]!=-1)
		{
			j = 1;
			while (data[i][j][0]!=-1)
			{
				ret = Get_String(data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1],buf,0);
				for (k=0;k<ret;k++)
				{
					str1[n] = buf[k];
					n++;
				}
				j++;
			}
		}
	}

	j = 0;
	for (i=n-1;i>=0;i--,j++)
		str[i] = str1[j];

	return (n);
}

static int Get_Pen_Data(unsigned char *p,int data[100][5000][3],int rect_data[4])
{
	int i,j,k;

	rect_data[0] = 10000;
	rect_data[1] = 10000;
	rect_data[2] = -1;
	rect_data[3] = -1;
	for (i=0;i<100;i++)
	{
		for (j=0;j<5000;j++)
		{
			for (k=0;k<3;k++)
				data[i][j][k] = -1;
		}
	}

	i = 0;
	j = 0;    // line
	k = 0;    // number
	while (p[i]!=0)
	{
		while (p[i]>='0' && p[i]<='9')
		{
			if (data[j][k][0]==-1) data[j][k][0] = p[i]-'0';
			else                   data[j][k][0] = data[j][k][0]*10+p[i]-'0';
			i++;
		}

		if (data[j][k][0]<rect_data[0]) rect_data[0] = data[j][k][0];
		if (data[j][k][0]>rect_data[2]) rect_data[2] = data[j][k][0];

		i++;  // comma skip
		while (p[i]>='0' && p[i]<='9')
		{
			if (data[j][k][1]==-1) data[j][k][1] = p[i]-'0';
			else                   data[j][k][1] = data[j][k][1]*10+p[i]-'0';
			i++;
		}

		i++; // comma skip
		while (p[i]>='0' && p[i]<='9')
		{
			if (data[j][k][2]==-1) data[j][k][2] = p[i]-'0';
			else                   data[j][k][2] = data[j][k][2]*10+p[i]-'0';
			i++;
		}

		if (data[j][k][1]<rect_data[1]) rect_data[1] = data[j][k][1];
		if (data[j][k][1]>rect_data[3]) rect_data[3] = data[j][k][1];

		i++;
		k++;

		if (p[i]==10) //10은 개행문자 \n이다.
		{
			j++;   // next line
			k = 0;
			i++;
		}
	}

	return (j);
}

static int Get_String(int x1,int y1,int x2,int y2,unsigned char *buf,int opt)
{
	// 앞쪽 진행 방향 
	if (x1<=x2)
	{
		// 아래쪽 방향 
		if (y1<=y2)
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'R','D',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf));
		}
		// 위쪽 방향 
		else
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'R','U',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf));
		}
	}
	// 뒤쪽 진행방향 
	else
	{
		// 아래쪽 방향 
		if (y1<=y2)
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'L','D',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf));
		}
		// 위쪽 방향 
		else
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'L','U',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf));
		}
	}
	return (0);
}

static int Get_Str(int xlen,int ylen,char c1,char c2,unsigned char *buf)
{
	int i,j,cha,cur1,cur2;

	if (xlen==0)
	{
		for (i=0;i<ylen;i++)
			buf[i] = c2;

		return (ylen);
	}
	else
		if (ylen==0)
		{
			for (i=0;i<xlen;i++)
				buf[i] = c1;

			return (xlen);
		}
		else
		{
			cha = xlen*1000/ylen;
			cur1 = cha;
			cur2 = 0;
			j = 0;
			for (i=0;i<xlen;i++)
			{
				while (cur1>=cur2 &&
					cur1<=cur2+1000)
				{
					buf[j] = c2;
					j++;
					cur1 += cha;
				}

				buf[j] = c1;
				j++;
				cur2 += 1000;
			}
			return (j);
		}
		return (0);
}

static int distance(unsigned char *string1,int len1,unsigned char *string2,int len2)
{
	int i,*end,*row;
	int *p,D,x;
	char char1,*char2p;

	if (len1==0 || len2==0) return (100000);
	while (len1>0 && len2>0 && *string1==*string2) 
	{
		len1--;
		len2--;
		string1++;
		string2++;
	}
	if (len1==0) return (len2);
	if (len2==0) return (len1);
	while (len1>0 && len2>0 && string1[len1-1]==string2[len2-1]) 
	{
		len1--;
		len2--;
	}
	if (len1==0) return (len2);
	if (len2==0) return (len1);  
	len1++;
	len2++;
	row = malloc(len2*sizeof(int));
	end = row+len2-1;
	for (i=0;i<len2;i++) row[i] = i;
	for (i=1;i<len1;i++) 
	{
		p = row+1;
		D = i;
		x = i;
		char1 = string1[i-1];
		char2p = string2;      
		while (p<=end) 
		{
			if (char1==*(char2p++)) x = --D;
			else				    x++;        
			D = (*p)+1;
			if (x>D) x = D;
			*(p++) = x;
		}
	}
	i = *end;
	free(row);
	return (i);
}



JNIEXPORT jint JNICALL Java_d2r_checksign_lib_FinoSign_checkSign(JNIEnv* env, jclass test, jbyteArray barr1, jbyteArray barr2) {

	

	int confi;
//	jsize size1;
//	jsize size2;
//	char* file1;
//	char* file2;
	unsigned char* first_file;
	unsigned char* Second_file;

	const char* file1;
	const char* file2;

	int len1;
	int len2;
	file1 = NULL;
	file2 = NULL;
	first_file = NULL;
	Second_file = NULL;
	confi = 0;
	len1 = 0;
	len2 = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "start");

	//printf("finotek_start\n");
	len1 = (int) (*env)->GetArrayLength(env, barr1);
	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "len1 = %d", len1);
	first_file = (unsigned char*) malloc(sizeof(unsigned char) * (len1 * 2));
	memset(first_file, 0x00, sizeof(unsigned char) * (len1 * 2));
	(*env)->GetByteArrayRegion(env, barr1, 0, len1, first_file);

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "first_file = %s", first_file);
	//printf("first_file: %s\n", first_file);

	len2 = (int) (*env)->GetArrayLength(env, barr2);
	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "len2 = %d", len2);
	Second_file = (unsigned char*) malloc(sizeof(unsigned char) * (len2 * 2) );
	memset(Second_file, 0x00, sizeof(unsigned char) * (len2 * 2));
	(*env)->GetByteArrayRegion(env, barr2, 0, len2, Second_file);

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "Second_file = %s", Second_file);

	//printf("Second_file: %s\n", Second_file);

// 	len1 = sizeof(unsigned char[len1]);
// 	first_file = malloc(sizeof(unsigned char[len1])); //unsigned char[len1];
// 	env->GetByteArrayRegion (barr1, 0, len, reinterpret_cast<jbyte*>(first_file));
// 
// 	len2 = env->GetArrayLength (barr2);
// 	Second_file = new unsigned char[len2];
// 	env->GetByteArrayRegion (barr2, 0, len2, reinterpret_cast<jbyte*>(Second_file));

// 	printf("finotek_start\n");
// 	file1 = (*env)->GetStringUTFChars(env, barr1, 0);
// 	strcpy((char*)first_file, file1);
// 	printf("file1=%s\n", file1);
// 	(*env)->ReleaseStringUTFChars(env, barr1, file1);
// 	printf("first_file=%s\n", first_file);
// 
// 	file2 = (*env)->GetStringUTFChars(env, barr2, 0);
// 	strcpy((char*)Second_file, file2);
// 	printf("file2=%s\n", file2);
// 	(*env)->ReleaseStringUTFChars(env, barr2, file2);
// 	printf("Second_file=%s\n", Second_file);



	//size1 = (*env)->GetArrayLength(env, barr1);
	//size2 = (*env)->GetArrayLength(env, barr2);

// 	file1 = (char*) (*env)->GetByteArrayElements(env, barr1, NULL);
// //	(*env)->ReleaseByteArrayElements(env, barr1, file1, JNI_ABORT);
// 
// 	printf("file1: %s \n", file1);
// 	file2 = (char*) (*env)->GetByteArrayElements(env, barr2, NULL);
// //	(*env)->ReleaseByteArrayElements(env, barr2, file2, JNI_ABORT);

//	printf("file2: %s \n", file2);

// 	first_file = (unsigned char*) file1;
// 	Second_file = (unsigned char*) file2;

// 	printf("first_file: %s \n", first_file);
// 	printf("Second_file: %s \n", Second_file);

	confi = Compare_FinoSign( first_file, Second_file );

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "result = %d", confi);

	first_file = NULL;
	Second_file = NULL;

	free(first_file);
	free(Second_file);

	//printf("result: %d \n", confi);
 	//printf("finotek_end\n");

	return confi;
}


/*

#include "d2r_checksign_lib_FinoSign.h"
#include <stdio.h>
#include <string.h>

#include <android/log.h>


#define TIME_LOCK 0

	//#include "resource.h"
	//#include "finosign.h"
	//#include <stdio.h>
#include <stdlib.h>				 
#include <math.h>
#include <time.h>

	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------

	static int pen_data1[100][5000][3],pen_data2[100][5000][3],pen_data3[100][5000][3];
static unsigned char str1[50000],str2[50000];
static int len1,len2,len3,len;
static int rect_data[2][4];
static int pnum1,pnum2;
static int m_nTotalPoint = 0;

// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------

static int Get_Length(int data[5000][3]);
static int Merge_Data1(int data1[100][5000][3],int data2[100][5000][3],int index);
static int Merge_Data2(int data1[100][5000][3],int data2[100][5000][3],int index1,int index2);
static int Merge_Data3(int data1[100][5000][3],int data2[100][5000][3],int index1,int index2,int index3);
static int Get_Speed(int data[5000][3]);
static int Get_Speed2(int data1[5000][3],int data2[5000][3]);
static int Get_Start_Locate(int data[5000][3],int *x,int *y);
static int Get_End_Locate(int data[5000][3],int *x,int *y);
static int Check_Segment(int num,int data1[100][5000][3],int data2[100][5000][3]);
static int Get_Image_Size(int data[5000][3],int range[4]);
static int Get_Percent(unsigned char *str1,int len1,unsigned char *str2,int len2);
static int Get_Normalize(int data[100][5000][3],int rect_data[4]);
static int Get_Normalize_Part(int data[5000][3],int rect_data[4]);
static unsigned char **Get_Image(int data[100][5000][3]);
static unsigned char **Get_Image_Part(int data[5000][3],int range[4]);
static int Set_Image(unsigned char **img,int xx1,int yy1,int xx2,int yy2);
static int Get_Data(unsigned char **img,unsigned char data[1250]);
static int Get_Total_String(unsigned char *str,int data[100][5000][3]);
static int Get_Object_String(unsigned char *str,int data[100][5000][3],int index1,int index2,int num);
static int Get_Object_String_Reverse(unsigned char *str,int data[100][5000][3],int index1,int index2,int num);
static int Get_Pen_Data(unsigned char *p,int data[100][5000][3],int rect_data[4]);
static int Get_String(int x1,int y1,int x2,int y2,unsigned char *buf,int opt);
static int Get_Str(int xlen,int ylen,char c1,char c2,unsigned char *buf);
static int distance(unsigned char *string1,int len1,unsigned char *string2,int len2);



// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------

// 서명을 3개로 분할한다. 
int Divide_Sign(unsigned char *sign_file,
				unsigned char *file1,
				unsigned char *file2,
				unsigned char *file3)
{
	unsigned char *p;
	int len,i;
	int s1,s2;
	FILE *fp;

	fp = fopen(sign_file,"rb");
	if (fp==0) return (0);
	fseek(fp,0,SEEK_END);
	len = ftell(fp);
	fseek(fp,0,SEEK_SET);
	p = malloc(len);
	fread(p,len,1,fp);

	for (i=0;i<len;i++)	p[i] = 255-p[i];

	s1 = len/3;
	s2 = len*2/3;

	fp = fopen(file1,"w+b");
	if (fp==0) return (0);
	for (i=0;i<s1;i++)	fwrite(&p[i],1,1,fp);
	fclose(fp);

	fp = fopen(file2,"w+b");
	if (fp==0) return (0);
	for (i=s1;i<s2;i++)	fwrite(&p[i],1,1,fp);
	fclose(fp);

	fp = fopen(file3,"w+b");
	if (fp==0) return (0);
	for (i=s2;i<len;i++) fwrite(&p[i],1,1,fp);
	fclose(fp);

	free(p);

	return (1);
}

// 3개로 분할된 서명정보를 1개로 합친다. 
int Merge_Sign(unsigned char *file1,
			   unsigned char *file2,
			   unsigned char *file3,
			   unsigned char *sign_file)
{
	unsigned char *p;
	FILE *fp,*wfp;
	int i,k,len;

	wfp = fopen(sign_file,"w+b");
	if (wfp==0) return (0);

	for (k=0;k<3;k++)
	{
		if (k==0) fp = fopen(file1,"rb");
		if (k==1) fp = fopen(file2,"rb");
		if (k==2) fp = fopen(file3,"rb");
		if (fp==0) return (0);
		fseek(fp,0,SEEK_END);
		len = ftell(fp);
		fseek(fp,0,SEEK_SET);
		p = malloc(len);
		fread(p,len,1,fp);
		for (i=0;i<len;i++) p[i] = 255-p[i];
		fclose(fp);
		fwrite(p,len,1,wfp);
		free(p);
	}

	fclose(wfp);
	return (1);
}

// ----------------------------------------------------
// 병렬처리 옵션
// ----------------------------------------------------

#define SOPT1 3      // 손떼기 획수의 오차 범위 
#define SOPT2 50     // 서명의 가로크기의 오차 비율범위(%) 
#define SOPT3 50     // 서명의 세로크기의 오차 비율범위(%) 
#define SOPT4 75	 // 가로,세로의 비율 허용 범위(%)
#define SOPT5 50	 // 서명 전체모양 비교에서 오차범위를 규정(%) 30
#define SOPT6 25	 // 전체 서명 속도 오차범위(%)  70
#define SOPT7 40	 // 전체 서명 흐름의 오차범위(%) 
#define SOPT8 500	 // 각각의 오브젝트의 서명 흐름의 오차범위(%) 
#define SOPT9 15	 // 각각의 오브젝트 사이의 서명 속도의 오차범위(%) 18
#define SOPT10 200   // 각각의 오브젝트의 시작좌표의 오차범위(pixel) 150
#define SOPT11 200   // 각각의 오브젝트의 끝좌표의 오차범위(pixel) 150
#define SOPT12 250   // 각각의 오브젝트의 가로크기의 오차범위(%)
#define SOPT13 300   // 각각의 오브젝트의 세로크기의 오차범위(%)
#define SOPT14 250   // 각각의 오브젝트의 가로세로 비율의 오차범위(%)
#define SOPT15 1000    // 각각의 오브젝트의 비트맵모양의 오차범위(%)

// 리턴값 : 2개의 서명간의 흐름 유사도
//p1, p2 서명데이터파일을 메모리에 읽은 값. 
//x좌표,y좌표,시간,x좌표,y좌표,시간 - - - - - - - - - - - - -  손을 떼었을 경우 라인변경
//x좌표,y좌표,시간,x좌표,y좌표,시간 - - - - - - - - - - - - -  손을 떼었을 경우 라인변경
//x좌표,y좌표,시간,x좌표,y좌표,시간 - - - - - - - - - - - - -  손을 떼었을 경우 라인변경

//int xs1 = 0;

int Compare_FinoSign(unsigned char *p1,unsigned char *p2) 
{
	int i,j,k,min,speed;
	int ret,dist,pass;
	int xs1,xs2,ys1,ys2,ratio1,ratio2;
	int sp1,sp2,start,end;

	//
	// 타임락을 체크한다. 
	//

#if TIME_LOCK
	struct tm *timeptr;
	time_t secsnow;

	timezone = 8*60*60;
	time(&secsnow);
	timeptr = localtime(&secsnow);
	if (timeptr->tm_year==116 || timeptr->tm_year==117)
	{
	}
	else
	{
		return (0);
	}
#endif

	// 	struct tm *timeptr;
	// 	time_t secsnow;
	// 
	// 	timezone = 8*60*60;
	// 	time(&secsnow);
	// 	timeptr = localtime(&secsnow);
	// 	if (timeptr->tm_year==116 || timeptr->tm_year==117)
	// 	{
	// 	}
	// 	else
	// 	{
	// 		return (0);
	// 	}


	//
	// 데이타를 읽어온다. 
	//
	pnum1 = Get_Pen_Data(p1,pen_data1,rect_data[0]);
	pnum2 = Get_Pen_Data(p2,pen_data2,rect_data[1]);

	// 손떼기 오차가 SOPT1이상이면 같은 서명으로 보지 않는다. 
	if (abs(pnum1-pnum2)>SOPT1)   
		return (0);

	//
	// 기본 서명 크기에 대한 정보를 구한다. 
	//

	xs1 = rect_data[0][2]-rect_data[0][0]+1;
	xs2 = rect_data[1][2]-rect_data[1][0]+1;
	ys1 = rect_data[0][3]-rect_data[0][1]+1;
	ys2 = rect_data[1][3]-rect_data[1][1]+1;
	ratio1 = xs1*100/xs2;
	ratio2 = ys1*100/ys2;

	//
	// 가로크기 50%
	//

	if (xs1>xs2)
	{
		if (xs2<xs1-xs1*SOPT2/100 || xs2>xs1+xs1*SOPT2/100)
		{
			//return (0);
			m_nTotalPoint += 5;
		}
	}
	else
	{
		if (xs1<xs2-xs2*SOPT2/100 || xs1>xs2+xs2*SOPT2/100)
		{
			//return (0);
			m_nTotalPoint += 5;
		}
	}

	//
	// 세로크기 50%
	//

	if (ys1>ys2)
	{
		if (ys2<ys1-ys1*SOPT3/100 || ys2>ys1+ys1*SOPT3/100)
		{
			//return (0);
			m_nTotalPoint += 5;
		}
	}
	else
	{
		if (ys1<ys2-ys2*SOPT3/100 || ys1>ys2+ys2*SOPT3/100)
		{
			//return (0);
			m_nTotalPoint += 5;
		}
	}

	//
	// 가로세로 비율의 범위
	//

	if (ratio1<ratio2-SOPT4 || ratio1>ratio2+SOPT4)
	{
		//return (0);
		m_nTotalPoint += 5;
	}


	//
	// 서명 크기를 동일한 크기로 맞추어 준다. 
	// normalize(500*250으로 맞춘다.)
	//

	Get_Normalize(pen_data1,rect_data[0]); //좌표 0, 0 기준으로 pen_data1 재배치
	Get_Normalize(pen_data2,rect_data[1]); //좌표 0, 0 기준으로 pen_data2 재배치

	// 
	// 전체 모양을 비교한다. 
	//

	{
		unsigned char **img1,**img2;
		unsigned char data1[1250],data2[1250];

		// 서명데이타를 bitmap으로 전환한다. 
		// 50X25의 작은 크기로 변환 
		img1 = Get_Image(pen_data1);
		img2 = Get_Image(pen_data2);	

		Get_Data(img1,data1);
		Get_Data(img2,data2);
		dist = distance(data1,1250,data2,1250);
		for (i=0;i<25;i++)
		{
			free(img1[i]);
			free(img2[i]);
		}
		free(img1);
		free(img2);

		// SOPT5%이상 오차가 발생하면 같은 서명으로 보지 않는다. 
		if (dist>1250*SOPT5/100)
		{
			//return (0);
			m_nTotalPoint += 5;
		}
	}

	//
	// 전체의 속도를 비교한다. 
	//

	// src 
	start = pen_data1[0][0][2];
	for (i=0;i<5000;i++)
	{
		if (pen_data1[pnum1-1][i][2]>-1 && pen_data1[pnum1-1][i+1][2]==-1)
		{
			end = pen_data1[pnum1-1][i][2];
			break;
		}
	}
	sp1 = end-start;

	// dst 
	start = pen_data2[0][0][2];
	for (i=0;i<5000;i++)
	{
		if (pen_data2[pnum2-1][i][2]>-1 && pen_data2[pnum2-1][i+1][2]==-1)
		{
			end = pen_data2[pnum2-1][i][2];
			break;
		}
	}
	sp2 = end-start;
	speed = sp1*SOPT6/100;
	//if (speed<50) speed = 50;
	if (sp2<sp1-speed || sp2>sp1+speed)
	{
		//return (0);
		m_nTotalPoint += 5;
	}

	//
	// 선의 흐름(각각의 오브젝트)
	//

	if (pnum1==pnum2)
	{
		if (Check_Segment(pnum1,pen_data1,pen_data2)==0)
			return (0);
	}
	else 
	{
		return (0);
	}


	//
	// 서명 전체의 선의 흐름
	//

	min = 100000;
	len1 = Get_Total_String(str1,pen_data1);
	len2 = Get_Total_String(str2,pen_data2);
	if (len1>len2+len2*SOPT7/100 || len2>len1+len1*SOPT7/100)
	{
		//return (0);
		m_nTotalPoint += 5;
	}

	dist = distance(str1,len1,str2,len2);
	ret = (10000-(dist*10000/((len1+len2))))+(int)sqrt(len1+len2)*10; //sprt 근사값 구하는 함수 (루트값)
	if (ret>=10000)	ret = 9999;
	if (ret<=0) ret = 0;
	if (ret<min) min = ret;

	m_nTotalPoint = m_nTotalPoint * 100;
	min = min - m_nTotalPoint;
	if (min < 0) {
		min = 0;
	} 

	return (min);
}

// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------

static int Get_Length(int data[5000][3])
{
	int i;

	for (i=0;i<5000;i++)
	{
		if (data[i][0]==-1)
			return (i);
	}
	return (0);
}

static int Merge_Data1(int data1[100][5000][3],int data2[100][5000][3],int index)
{
	int i,j,ii,jj,l1,l2;

	i = 0;
	j = 0;
	while (data2[i][0][0]!=-1)
	{
		if (i!=index)
		{
			for (ii=0;ii<5000;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}
			j++;
		}
		else
		{
			l1 = Get_Length(data2[j]);
			l2 = Get_Length(data2[j+1]);
			for (ii=0;ii<l1;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}

			for (ii=0;ii<l2;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii+l1][jj] = data2[j+1][ii][jj];
			}

			data1[i][ii+l1][0] = -1;
			data1[i][ii+l1][1] = -1;
			data1[i][ii+l1][2] = -1;
			j += 2;
		}
		i++;
	}
	return (1);
}

static int Merge_Data2(int data1[100][5000][3],int data2[100][5000][3],int index1,int index2)
{
	int i,j,ii,jj,l1,l2;

	i = 0;
	j = 0;
	while (data2[i][0][0]!=-1)
	{
		if (i!=index1 && i!=index2)
		{
			for (ii=0;ii<5000;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}
			j++;
		}
		else
		{
			l1 = Get_Length(data2[j]);
			l2 = Get_Length(data2[j+1]);
			for (ii=0;ii<l1;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}

			for (ii=0;ii<l2;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii+l1][jj] = data2[j+1][ii][jj];
			}

			data1[i][ii+l1][0] = -1;
			data1[i][ii+l1][1] = -1;
			data1[i][ii+l1][2] = -1;
			j += 2;
		}
		i++;
	}
	return (1);
}

static int Merge_Data3(int data1[100][5000][3],int data2[100][5000][3],int index1,int index2,int index3)
{
	int i,j,ii,jj,l1,l2;

	i = 0;
	j = 0;
	while (data2[i][0][0]!=-1)
	{
		if (i!=index1 && i!=index2 && i!=index3)
		{
			for (ii=0;ii<5000;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}
			j++;
		}
		else
		{
			l1 = Get_Length(data2[j]);
			l2 = Get_Length(data2[j+1]);
			for (ii=0;ii<l1;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii][jj] = data2[j][ii][jj];
			}

			for (ii=0;ii<l2;ii++)
			{
				for (jj=0;jj<3;jj++)
					data1[i][ii+l1][jj] = data2[j+1][ii][jj];
			}

			data1[i][ii+l1][0] = -1;
			data1[i][ii+l1][1] = -1;
			data1[i][ii+l1][2] = -1;
			j += 2;
		}
		i++;
	}
	return (1);
}

static int Get_Speed(int data[5000][3])
{
	int len;

	len = Get_Length(data);
	return (data[len-1][2]-data[0][2]);
}

static int Get_Speed2(int data1[5000][3],int data2[5000][3])
{
	int len;

	len = Get_Length(data1);
	return (data2[0][2]-data1[len-1][2]);
}

static int Get_Start_Locate(int data[5000][3],int *x,int *y)
{
	*x = data[0][0];
	*y = data[0][1];
	return (1);
}

static int Get_End_Locate(int data[5000][3],int *x,int *y)
{
	int len;

	len = Get_Length(data);
	*x = data[len-1][0];
	*y = data[len-1][1];
	return (1);
}

static int Check_Segment(int num,int data1[100][5000][3],int data2[100][5000][3])
{
	int i,j,len1,len2,ret;
	int sp1,sp2;
	int x1,y1,x2,y2;

	//
	// 각각의 세그먼트간의 선의 흐름의 distance를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		len1 = Get_Object_String(str1,data1,i,i,num);
		len2 = Get_Object_String(str2,data2,i,i,num);
		if (len1>100 || len2>100)
		{
			ret = Get_Percent(str1,len1,str2,len2);
			if (ret<5000)
			{
				//return (0);
				m_nTotalPoint += 5;
			}

			if (len1>len2+len2*SOPT8/100 || len2>len1+len1*SOPT8/100)
			{
				//return (0);
				m_nTotalPoint += 5;
			}
		}
	}

	//
	// 각각의 세그먼트 속도를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		sp1 = Get_Speed(data1[i]);
		sp2 = Get_Speed(data2[i]);
		if (sp1>100 || sp2>100)
		{
			if (sp1>sp2*SOPT9/10 || sp2>sp1*SOPT9/10)
			{
				//return (0);
				m_nTotalPoint += 5;
			}
		}
	}

	//
	// 각각의 세그먼트사이의 속도를 계산한다.
	//

	for (i=0;i<num-1;i++)
	{
		sp1 = Get_Speed2(data1[i],data1[i+1]);
		sp2 = Get_Speed2(data2[i],data2[i+1]);
		if (sp1>100 || sp2>100)
		{
			if (sp1>sp2*SOPT9/10 || sp2>sp1*SOPT9/10)
			{
				//return (0);
				m_nTotalPoint += 5;
			}
		}
	}

	//
	// 시작좌표의 오차를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		Get_Start_Locate(data1[i],&x1,&y1);
		Get_Start_Locate(data2[i],&x2,&y2);
		if (abs(x1-x2)>SOPT10 || abs(y1-y2)>SOPT10)
		{
			//return (0);
			m_nTotalPoint += 5;
		}
	}

	//
	// 끝나는 좌표의 오차를 계산한다. 
	//

	for (i=0;i<num;i++)
	{
		Get_End_Locate(data1[i],&x1,&y1);
		Get_End_Locate(data2[i],&x2,&y2);
		if (abs(x1-x2)>SOPT11 || abs(y1-y2)>SOPT11)
		{
			//return (0);
			m_nTotalPoint += 5;
		}
	}
	if (num<=1)
		return (1);

	//
	// 각각의 오브젝트의 모양을 확인한다. 
	//

	for (i=0;i<num;i++)
	{
		int xs1,ys1,xs2,ys2,range1[4],range2[4];
		int ratio1,ratio2;

		Get_Image_Size(data1[i],range1);
		Get_Image_Size(data2[i],range2);
		xs1 = range1[2]-range1[0]+1;
		ys1 = range1[3]-range1[1]+1;
		xs2 = range2[2]-range2[0]+1;
		ys2 = range2[3]-range2[1]+1;

		// 각각의 가로 획의 크기를 비교한다. 
		if (xs1>30 && xs2>30)
		{
			if (xs1>xs2*SOPT12/100 || xs2>xs1*SOPT12/100)
			{
				//return (0);
				m_nTotalPoint += 5;
			}
		}

		// 각각의 세로 획의 크기를 비교한다. 		
		if (ys1>30 && ys2>30)
		{
			if (ys1>ys2*SOPT13/100 || ys2>ys1*SOPT13/100)
			{
				//return (0);
				m_nTotalPoint += 5;
			}
		}

		// 각각의 가로세로 비율을 확인한다. 
		if (xs1>30 && xs2>30 && ys1>30 && ys2>30)
		{
			ratio1 = xs1*100/ys1;
			ratio2 = xs2*100/ys2;
			if (ratio1>ratio2*SOPT14/100 || ratio2>ratio1*SOPT14/100)
			{
				//return (0);
				m_nTotalPoint += 5;
			}
		}

		// 최종 획의 모양
		if ((xs1>30 && ys1>15) || (xs2>30 && ys2>15))
		{
			int dist;
			unsigned char **img1,**img2;
			unsigned char dat1[1250],dat2[1250];

			img1 = Get_Image_Part(data1[i],range1);
			img2 = Get_Image_Part(data2[i],range2);	

			Get_Data(img1,dat1);
			Get_Data(img2,dat2);
			dist = distance(dat1,1250,dat2,1250);
			for (j=0;j<25;j++)
			{
				free(img1[j]);
				free(img2[j]);
			}
			free(img1);
			free(img2);

			if (dist>SOPT15)
			{
				//return (0);
				m_nTotalPoint += 5;
			}
		}
	}

	return (1);
}

static int Get_Image_Size(int data[5000][3],int range[4])
{
	int j,x1,y1,x2,y2;

	x1 = 1000000;
	y1 = 1000000;
	x2 = -1;
	y2 = -1;

	// 기본 좌표를 0,0으로 맞춘다. 
	j = 0;
	while (data[j][0]!=-1)
	{
		if (data[j][0]<x1) x1 = data[j][0];
		if (data[j][1]<y1) y1 = data[j][1];
		if (data[j][0]>x2) x2 = data[j][0];
		if (data[j][1]>y2) y2 = data[j][1];
		j++;
	}

	range[0] = x1;
	range[1] = y1;
	range[2] = x2;
	range[3] = y2;

	return (1);
}

static int Get_Percent(unsigned char *str1,int len1,unsigned char *str2,int len2)
{
	int dist,ret;

	if (len1==0 || len2==0)
		return (0);

	dist = distance(str1,len1,str2,len2);
	ret = (10000-(dist*10000/((len1+len2))))+(int)sqrt(len1+len2)*10;
	return (ret);
}

static int Get_Normalize(int data[100][5000][3],int rect_data[4])
{
	int i,j,xfac,yfac;
	int xs,ys;

	// 기본 좌표를 0,0으로 맞춘다. 
	i = 0;
	while (data[i][0][0]!=-1)
	{
		j = 0;
		while (data[i][j][0]!=-1)
		{
			data[i][j][0] -= rect_data[0];
			data[i][j][1] -= rect_data[1];
			j++;
		}
		i++;
	}

	// 사이즈를 500,250으로 맞추어준다. 
	xs = rect_data[2]-rect_data[0]+1;
	ys = rect_data[3]-rect_data[1]+1;
	xfac = xs*10000/500;
	yfac = ys*10000/250;

	i = 0;
	while (data[i][0][0]!=-1)
	{
		j = 0;
		while (data[i][j][0]!=-1)
		{
			data[i][j][0] = data[i][j][0]*10000/xfac;
			data[i][j][1] = data[i][j][1]*10000/yfac;
			j++;
		}
		i++;
	}
	return (1);
}

static int Get_Normalize_Part(int data[5000][3],int rect_data[4])
{
	int j,xfac,yfac;
	int xs,ys;

	// 기본 좌표를 0,0으로 맞춘다. 
	j = 0;
	while (data[j][0]!=-1)
	{
		data[j][0] -= rect_data[0];
		data[j][1] -= rect_data[1];
		j++;
	}

	// 사이즈를 500,250으로 맞추어준다. 
	xs = rect_data[2]-rect_data[0]+1;
	ys = rect_data[3]-rect_data[1]+1;
	xfac = xs*10000/500;
	yfac = ys*10000/250;

	j = 0;
	while (data[j][0]!=-1)
	{
		data[j][0] = data[j][0]*10000/xfac;
		data[j][1] = data[j][1]*10000/yfac;
		j++;
	}
	return (1);
}

static unsigned char **Get_Image_Part(int data[5000][3],int range[4])
{
	int i,j,xs,ys,buf[5000][3];
	unsigned char **img;

	for (i=0;i<5000;i++)
	{
		for (j=0;j<3;j++)
			buf[i][j] = data[i][j];
	}

	Get_Normalize_Part(buf,range);

	xs = 50;
	ys = 25;
	img = (unsigned char **)malloc(ys*sizeof(unsigned char *));
	for (i=0;i<ys;i++)
	{
		img[i] = (unsigned char *)malloc(xs);
		for (j=0;j<xs;j++)
			img[i][j] = 0;
	}

	j = 1;
	while (buf[j][0]!=-1)
	{
		Set_Image(img,buf[j-1][0],buf[j-1][1],buf[j][0],buf[j][1]);
		j++;
	}

	return (img);
}

static unsigned char **Get_Image(int data[100][5000][3])
{
	int i,j,xs,ys;
	unsigned char **img;

	xs = 50;
	ys = 25;

	img = (unsigned char **)malloc(ys*sizeof(unsigned char *));
	for (i=0;i<ys;i++)
	{
		img[i] = (unsigned char *)malloc(xs);
		for (j=0;j<xs;j++)
			img[i][j] = 0;
	}

	i = 0;
	while (data[i][0][0]!=-1)
	{
		j = 1;
		while (data[i][j][0]!=-1)
		{
			Set_Image(img,data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1]);
			j++;
		}
		i++;
	}
	return (img);
}

static int Set_Image(unsigned char **img,int xx1,int yy1,int xx2,int yy2)
{
	int x1,y1,x2,y2,xlen,ylen;
	int i,j,aa,b;

	if (xx1<0 || xx1>=500 || xx2<0 || xx2>=500)
		return (0);

	if (yy1<0 || yy1>=250 || yy2<0 || yy2>=250)
		return (0);

	xlen = abs(xx1-xx2);
	ylen = abs(yy1-yy2);
	if (xlen>ylen)
	{		
		if (xx1<=xx2)
		{
			x1 = xx1;
			y1 = yy1;
			x2 = xx2;
			y2 = yy2;
		}
		else
		{
			x1 = xx2;
			y1 = yy2;
			x2 = xx1;
			y2 = yy1;
		}

		img[y1/10][x1/10] = 1;
		img[y2/10][x2/10] = 1;
		for (j=x1+1;j<x2-1;j++)
		{
			aa = ylen*10000/xlen;
			b = aa*(j-x1-1)/10000;
			if (aa%10000>5000) b++;

			if (y1<y2) img[(y1+b)/10][j/10] = 1;
			else       img[(y1-b)/10][j/10] = 1;
		}
	}
	else
	{
		if (yy1<=yy2)
		{
			x1 = xx1;
			y1 = yy1;
			x2 = xx2;
			y2 = yy2;
		}
		else
		{
			x1 = xx2;
			y1 = yy2;
			x2 = xx1;
			y2 = yy1;
		}

		img[y1/10][x1/10] = 1;
		img[y2/10][x2/10] = 1;
		for (i=y1+1;i<y2-1;i++)
		{
			aa = xlen*10000/ylen;
			b = aa*(i-y1-1)/10000;
			if (aa%10000>5000) b++;

			if (x1<x2) img[i/10][(x1+b)/10] = 1;
			else       img[i/10][(x1-b)/10] = 1;
		}
	}
	return (1);
}

static int Get_Data(unsigned char **img,unsigned char data[1250])
{
	int i,j,k;

	k = 0;
	for (i=0;i<25;i+=2)
	{
		for (j=0;j<50;j++,k++)
		{
			if (img[i][j]==1) data[k] = 'D';
			else			  data[k] = 'B';
		}

		if (i<24)
		{
			for (j=49;j>=0;j--,k++)
			{
				if (img[i+1][j]==1) data[k] = 'D';
				else			    data[k] = 'B';
			}
		}
	}
	return (1);
}

static int Get_Total_String(unsigned char *str,int data[100][5000][3])
{
	int i,j,k,n,ret;
	unsigned char buf[10000];

	i = 0;
	n = 0;
	while (data[i][0][0]!=-1)
	{
		j = 1;
		while (data[i][j][0]!=-1)
		{
			ret = Get_String(data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1],buf,0);
			for (k=0;k<ret;k++)
			{
				str[n] = buf[k];
				n++;
			}
			j++;
		}
		i++;
		if (n>0 && data[i][0][0]!=-1)
		{
			ret = Get_String(data[i-1][j-1][0],data[i-1][j-1][1],data[i][0][0],data[i][0][1],buf,1);
			for (k=0;k<ret;k++)
			{
				str[n] = buf[k];
				n++;
			}
		}
	}

	return (n);
}

static int Get_Object_String(unsigned char *str,int data[100][5000][3],int index1,int index2,int num)
{
	int i,j,k,n,ret;
	unsigned char buf[10000];

	if (index1<0) 
		return (0);

	if (index2>num-1)
		return (0);

	n = 0;
	for (i=index1;i<=index2;i++)
	{
		if (data[i][0][0]!=-1)
		{
			j = 1;
			while (data[i][j][0]!=-1)
			{
				ret = Get_String(data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1],buf,0);
				for (k=0;k<ret;k++)
				{
					str[n] = buf[k];
					n++;
				}
				j++;
			}
		}
	}

	return (n);
}

static int Get_Object_String_Reverse(unsigned char *str,int data[100][5000][3],int index1,int index2,int num)
{
	int i,j,k,n,ret;
	unsigned char buf[10000],str1[10000];

	if (index1<0) 
		return (0);

	if (index2>num-1)
		return (0);

	n = 0;
	for (i=index1;i<=index2;i++)
	{
		if (data[i][0][0]!=-1)
		{
			j = 1;
			while (data[i][j][0]!=-1)
			{
				ret = Get_String(data[i][j-1][0],data[i][j-1][1],data[i][j][0],data[i][j][1],buf,0);
				for (k=0;k<ret;k++)
				{
					str1[n] = buf[k];
					n++;
				}
				j++;
			}
		}
	}

	j = 0;
	for (i=n-1;i>=0;i--,j++)
		str[i] = str1[j];

	return (n);
}

static int Get_Pen_Data(unsigned char *p,int data[100][5000][3],int rect_data[4])
{
	int i,j,k;

	rect_data[0] = 10000;
	rect_data[1] = 10000;
	rect_data[2] = -1;
	rect_data[3] = -1;
	for (i=0;i<100;i++)
	{
		for (j=0;j<5000;j++)
		{
			for (k=0;k<3;k++)
				data[i][j][k] = -1;
		}
	}

	i = 0;
	j = 0;    // line
	k = 0;    // number
	while (p[i]!=0)
	{
		while (p[i]>='0' && p[i]<='9')
		{
			if (data[j][k][0]==-1) data[j][k][0] = p[i]-'0';
			else                   data[j][k][0] = data[j][k][0]*10+p[i]-'0';
			i++;
		}

		if (data[j][k][0]<rect_data[0]) rect_data[0] = data[j][k][0];
		if (data[j][k][0]>rect_data[2]) rect_data[2] = data[j][k][0];

		i++;  // comma skip
		while (p[i]>='0' && p[i]<='9')
		{
			if (data[j][k][1]==-1) data[j][k][1] = p[i]-'0';
			else                   data[j][k][1] = data[j][k][1]*10+p[i]-'0';
			i++;
		}

		i++; // comma skip
		while (p[i]>='0' && p[i]<='9')
		{
			if (data[j][k][2]==-1) data[j][k][2] = p[i]-'0';
			else                   data[j][k][2] = data[j][k][2]*10+p[i]-'0';
			i++;
		}

		if (data[j][k][1]<rect_data[1]) rect_data[1] = data[j][k][1];
		if (data[j][k][1]>rect_data[3]) rect_data[3] = data[j][k][1];

		i++;
		k++;

		if (p[i]==10) //10은 개행문자 \n이다.
		{
			j++;   // next line
			k = 0;
			i++;
		}
	}

	return (j);
}

static int Get_String(int x1,int y1,int x2,int y2,unsigned char *buf,int opt)
{
	// 앞쪽 진행 방향 
	if (x1<=x2)
	{
		// 아래쪽 방향 
		if (y1<=y2)
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'R','D',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf));
		}
		// 위쪽 방향 
		else
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'R','U',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf));
		}
	}
	// 뒤쪽 진행방향 
	else
	{
		// 아래쪽 방향 
		if (y1<=y2)
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'L','D',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf));
		}
		// 위쪽 방향 
		else
		{
			if (1)
				//			if (opt==0)
					return (Get_Str(abs(x1-x2),abs(y1-y2),'L','U',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','u',buf));
		}
	}
	return (0);
}

static int Get_Str(int xlen,int ylen,char c1,char c2,unsigned char *buf)
{
	int i,j,cha,cur1,cur2;

	if (xlen==0)
	{
		for (i=0;i<ylen;i++)
			buf[i] = c2;

		return (ylen);
	}
	else
		if (ylen==0)
		{
			for (i=0;i<xlen;i++)
				buf[i] = c1;

			return (xlen);
		}
		else
		{
			cha = xlen*1000/ylen;
			cur1 = cha;
			cur2 = 0;
			j = 0;
			for (i=0;i<xlen;i++)
			{
				while (cur1>=cur2 &&
					cur1<=cur2+1000)
				{
					buf[j] = c2;
					j++;
					cur1 += cha;
				}

				buf[j] = c1;
				j++;
				cur2 += 1000;
			}
			return (j);
		}
		return (0);
}

static int distance(unsigned char *string1,int len1,unsigned char *string2,int len2)
{
	int i,*end,*row;
	int *p,D,x;
	char char1,*char2p;

	if (len1==0 || len2==0) return (100000);
	while (len1>0 && len2>0 && *string1==*string2) 
	{
		len1--;
		len2--;
		string1++;
		string2++;
	}
	if (len1==0) return (len2);
	if (len2==0) return (len1);
	while (len1>0 && len2>0 && string1[len1-1]==string2[len2-1]) 
	{
		len1--;
		len2--;
	}
	if (len1==0) return (len2);
	if (len2==0) return (len1);  
	len1++;
	len2++;
	row = malloc(len2*sizeof(int));
	end = row+len2-1;
	for (i=0;i<len2;i++) row[i] = i;
	for (i=1;i<len1;i++) 
	{
		p = row+1;
		D = i;
		x = i;
		char1 = string1[i-1];
		char2p = string2;      
		while (p<=end) 
		{
			if (char1==*(char2p++)) x = --D;
			else				    x++;        
			D = (*p)+1;
			if (x>D) x = D;
			*(p++) = x;
		}
	}
	i = *end;
	free(row);
	return (i);
}



JNIEXPORT jint JNICALL Java_d2r_checksign_lib_FinoSign_checkSign(JNIEnv* env, jclass test, jbyteArray barr1, jbyteArray barr2) {



	int confi;
	//	jsize size1;
	//	jsize size2;
	//	char* file1;
	//	char* file2;
	unsigned char* first_file;
	unsigned char* Second_file;

	const char* file1;
	const char* file2;

	int len1;
	int len2;
	file1 = NULL;
	file2 = NULL;
	first_file = NULL;
	Second_file = NULL;
	confi = 0;
	len1 = 0;
	len2 = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "start");

	//printf("finotek_start\n");
	len1 = (int) (*env)->GetArrayLength(env, barr1);
	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "len1 = %d", len1);
	first_file = (unsigned char*) malloc(sizeof(unsigned char) * (len1 * 2));
	memset(first_file, 0x00, sizeof(unsigned char) * (len1 * 2));
	(*env)->GetByteArrayRegion(env, barr1, 0, len1, first_file);

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "first_file = %s", first_file);
	//printf("first_file: %s\n", first_file);

	len2 = (int) (*env)->GetArrayLength(env, barr2);
	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "len2 = %d", len2);
	Second_file = (unsigned char*) malloc(sizeof(unsigned char) * (len2 * 2) );
	memset(Second_file, 0x00, sizeof(unsigned char) * (len2 * 2));
	(*env)->GetByteArrayRegion(env, barr2, 0, len2, Second_file);

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "Second_file = %s", Second_file);

	//printf("Second_file: %s\n", Second_file);

	// 	len1 = sizeof(unsigned char[len1]);
	// 	first_file = malloc(sizeof(unsigned char[len1])); //unsigned char[len1];
	// 	env->GetByteArrayRegion (barr1, 0, len, reinterpret_cast<jbyte*>(first_file));
	// 
	// 	len2 = env->GetArrayLength (barr2);
	// 	Second_file = new unsigned char[len2];
	// 	env->GetByteArrayRegion (barr2, 0, len2, reinterpret_cast<jbyte*>(Second_file));

	// 	printf("finotek_start\n");
	// 	file1 = (*env)->GetStringUTFChars(env, barr1, 0);
	// 	strcpy((char*)first_file, file1);
	// 	printf("file1=%s\n", file1);
	// 	(*env)->ReleaseStringUTFChars(env, barr1, file1);
	// 	printf("first_file=%s\n", first_file);
	// 
	// 	file2 = (*env)->GetStringUTFChars(env, barr2, 0);
	// 	strcpy((char*)Second_file, file2);
	// 	printf("file2=%s\n", file2);
	// 	(*env)->ReleaseStringUTFChars(env, barr2, file2);
	// 	printf("Second_file=%s\n", Second_file);



	//size1 = (*env)->GetArrayLength(env, barr1);
	//size2 = (*env)->GetArrayLength(env, barr2);

	// 	file1 = (char*) (*env)->GetByteArrayElements(env, barr1, NULL);
	// //	(*env)->ReleaseByteArrayElements(env, barr1, file1, JNI_ABORT);
	// 
	// 	printf("file1: %s \n", file1);
	// 	file2 = (char*) (*env)->GetByteArrayElements(env, barr2, NULL);
	// //	(*env)->ReleaseByteArrayElements(env, barr2, file2, JNI_ABORT);

	//	printf("file2: %s \n", file2);

	// 	first_file = (unsigned char*) file1;
	// 	Second_file = (unsigned char*) file2;

	// 	printf("first_file: %s \n", first_file);
	// 	printf("Second_file: %s \n", Second_file);

	confi = Compare_FinoSign( first_file, Second_file );

	__android_log_print(ANDROID_LOG_DEBUG, "finotek", "result = %d", confi);

	first_file = NULL;
	Second_file = NULL;

	free(first_file);
	free(Second_file);

	//printf("result: %d \n", confi);
	//printf("finotek_end\n");

	return confi;
}


*/