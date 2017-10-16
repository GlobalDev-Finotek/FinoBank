
#define TIME_LOCK 0

//#include "resource.h"
//#include "finosign.h"
#include <stdio.h>
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

// ������ 3���� �����Ѵ�. 
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

// 3���� ���ҵ� ���������� 1���� ��ģ��. 
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
// ����ó�� �ɼ�
// ----------------------------------------------------

#define SOPT1 3      // �ն��� ȹ���� ���� ���� 
#define SOPT2 50     // ������ ����ũ���� ���� ��������(%) 
#define SOPT3 50     // ������ ����ũ���� ���� ��������(%) 
#define SOPT4 75	 // ����,������ ���� ��� ����(%)
#define SOPT5 30	 // ���� ��ü��� �񱳿��� ���������� ����(%)
#define SOPT6 70	 // ��ü ���� �ӵ� ��������(%)
#define SOPT7 40	 // ��ü ���� �帧�� ��������(%) 
#define SOPT8 500	 // ������ ������Ʈ�� ���� �帧�� ��������(%) 
#define SOPT9 18	 // ������ ������Ʈ ������ ���� �ӵ��� ��������(%) 
#define SOPT10 150   // ������ ������Ʈ�� ������ǥ�� ��������(pixel)
#define SOPT11 150   // ������ ������Ʈ�� ����ǥ�� ��������(pixel)
#define SOPT12 250   // ������ ������Ʈ�� ����ũ���� ��������(%)
#define SOPT13 300   // ������ ������Ʈ�� ����ũ���� ��������(%)
#define SOPT14 250   // ������ ������Ʈ�� ���μ��� ������ ��������(%)
#define SOPT15 1000    // ������ ������Ʈ�� ��Ʈ�ʸ���� ��������(%)

// ���ϰ� : 2���� ������ �帧 ���絵
//p1, p2 �������������� �޸𸮿� ���� ��. 
//x��ǥ,y��ǥ,�ð�,x��ǥ,y��ǥ,�ð� - - - - - - - - - - - - -  ���� ������ ��� ���κ���
//x��ǥ,y��ǥ,�ð�,x��ǥ,y��ǥ,�ð� - - - - - - - - - - - - -  ���� ������ ��� ���κ���
//x��ǥ,y��ǥ,�ð�,x��ǥ,y��ǥ,�ð� - - - - - - - - - - - - -  ���� ������ ��� ���κ���

//int xs1 = 0;

int Compare_FinoSign(unsigned char *p1,unsigned char *p2) 
{
	int i,j,k,min,speed;
	int ret,dist,pass;
	int xs1,xs2,ys1,ys2,ratio1,ratio2;
	int sp1,sp2,start,end;

	//
	// Ÿ�Ӷ��� üũ�Ѵ�. 
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
	// ����Ÿ�� �о�´�. 
	//
	pnum1 = Get_Pen_Data(p1,pen_data1,rect_data[0]);
	pnum2 = Get_Pen_Data(p2,pen_data2,rect_data[1]);
	
	// �ն��� ������ SOPT1�̻��̸� ���� �������� ���� �ʴ´�. 
	if (abs(pnum1-pnum2)>SOPT1)   
		return (0);

	//
	// �⺻ ���� ũ�⿡ ���� ������ ���Ѵ�. 
	//

	xs1 = rect_data[0][2]-rect_data[0][0]+1;
	xs2 = rect_data[1][2]-rect_data[1][0]+1;
	ys1 = rect_data[0][3]-rect_data[0][1]+1;
	ys2 = rect_data[1][3]-rect_data[1][1]+1;
	ratio1 = xs1*100/xs2;
	ratio2 = ys1*100/ys2;

	//
	// ����ũ�� 50%
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
	// ����ũ�� 50%
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
	// ���μ��� ������ ����
	//

	if (ratio1<ratio2-SOPT4 || ratio1>ratio2+SOPT4)
		return (0);

	//
	// ���� ũ�⸦ ������ ũ��� ���߾� �ش�. 
	// normalize(500*250���� �����.)
	//

	Get_Normalize(pen_data1,rect_data[0]); //��ǥ 0, 0 �������� pen_data1 ���ġ
	Get_Normalize(pen_data2,rect_data[1]); //��ǥ 0, 0 �������� pen_data2 ���ġ

	// 
	// ��ü ����� ���Ѵ�. 
	//

	{
		unsigned char **img1,**img2;
		unsigned char data1[1250],data2[1250];
		
		// ������Ÿ�� bitmap���� ��ȯ�Ѵ�. 
		// 50X25�� ���� ũ��� ��ȯ 
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

		// SOPT5%�̻� ������ �߻��ϸ� ���� �������� ���� �ʴ´�. 
		if (dist>1250*SOPT5/100)
			return (0);
	}

	//
	// ��ü�� �ӵ��� ���Ѵ�. 
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
	if (speed<50) speed = 50;
	if (sp2<sp1-speed || sp2>sp1+speed)
		return (0);

	//
	// ���� �帧(������ ������Ʈ)
	//

	if (pnum1==pnum2)
	{
		if (Check_Segment(pnum1,pen_data1,pen_data2)==0)
			return (0);
	}
	else
	if (pnum2==pnum1-1)
	{
		pass = 0;
		for (i=0;i<pnum1-1;i++)		
		{
			Merge_Data1(pen_data3,pen_data1,i);
			if (Check_Segment(pnum2,pen_data2,pen_data3)==1)
			{
				pass = 1;
				break;
			}
		}
		if (pass==0)
			return (0);
	}
	else
	if (pnum1==pnum2-1)
	{
		pass = 0;
		for (i=0;i<pnum2-1;i++)		
		{
			Merge_Data1(pen_data3,pen_data2,i);
			if (Check_Segment(pnum1,pen_data1,pen_data3)==1)
			{
				pass = 1;
				break;
			}
		}
		if (pass==0)
			return (0);
	}
	else
	if (pnum2==pnum1-2)
	{
		pass = 0;
		for (i=0;i<pnum1-1;i++)	
		{
			for (j=i+2;j<pnum1-1;j++)
			{
				Merge_Data2(pen_data3,pen_data1,i,j);
				if (Check_Segment(pnum2,pen_data2,pen_data3)==1)
				{
					pass = 1;
					i = 1000;
					j = 1000;
				}
			}
		}
		if (pass==0)
			return (0);
	}
	else
	if (pnum1==pnum2-2)
	{
		pass = 0;
		for (i=0;i<pnum2-1;i++)	
		{
			for (j=i+2;j<pnum2-1;j++)
			{
				Merge_Data2(pen_data3,pen_data2,i,j);
				if (Check_Segment(pnum1,pen_data1,pen_data3)==1)
				{
					pass = 1;
					i = 10000;
					j = 10000;
				}
			}
		}
		if (pass==0)
			return (0);
	}
	else
	if (pnum2==pnum1-3)
	{
		pass = 0;
		for (i=0;i<pnum1-1;i++)	
		{
			for (j=i+2;j<pnum1-1;j++)
			{
				for (k=j+2;k<pnum1-1;k++)
				{
					Merge_Data3(pen_data3,pen_data1,i,j,k);
					if (Check_Segment(pnum2,pen_data2,pen_data3)==1)
					{
						pass = 1;
						i = 1000;
						j = 1000;
						k = 1000;
					}
				}
			}
		}
		if (pass==0)
			return (0);
	}
	else
	if (pnum1==pnum2-3)
	{
		pass = 0;
		for (i=0;i<pnum2-1;i++)	
		{
			for (j=i+2;j<pnum2-1;j++)
			{
				for (k=j+2;k<pnum2-1;k++)
				{
					Merge_Data3(pen_data3,pen_data2,i,j,k);
					if (Check_Segment(pnum1,pen_data1,pen_data3)==1)
					{
						pass = 1;
						i = 10000;
						j = 10000;
						k = 10000;
					}
				}
			}
		}
		if (pass==0)
			return (0);
	}

	//
	// ���� ��ü�� ���� �帧
	//

	min = 100000;
	len1 = Get_Total_String(str1,pen_data1);
	len2 = Get_Total_String(str2,pen_data2);
	if (len1>len2+len2*SOPT7/100 || len2>len1+len1*SOPT7/100)
		return (0);

	dist = distance(str1,len1,str2,len2);
	ret = (10000-(dist*10000/((len1+len2))))+(int)sqrt(len1+len2)*10; //sprt �ٻ簪 ���ϴ� �Լ� (��Ʈ��)
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
	// ������ ���׸�Ʈ���� ���� �帧�� distance�� ����Ѵ�. 
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
	// ������ ���׸�Ʈ �ӵ��� ����Ѵ�. 
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
	// ������ ���׸�Ʈ������ �ӵ��� ����Ѵ�.
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
	// ������ǥ�� ������ ����Ѵ�. 
	//

	for (i=0;i<num;i++)
	{
		Get_Start_Locate(data1[i],&x1,&y1);
		Get_Start_Locate(data2[i],&x2,&y2);
		if (abs(x1-x2)>SOPT10 || abs(y1-y2)>SOPT10)
			return (0);
	}

	//
	// ������ ��ǥ�� ������ ����Ѵ�. 
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
	// ������ ������Ʈ�� ����� Ȯ���Ѵ�. 
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

		// ������ ���� ȹ�� ũ�⸦ ���Ѵ�. 
		if (xs1>30 && xs2>30)
		{
			if (xs1>xs2*SOPT12/100 || xs2>xs1*SOPT12/100)
				return (0);
		}

		// ������ ���� ȹ�� ũ�⸦ ���Ѵ�. 		
		if (ys1>30 && ys2>30)
		{
			if (ys1>ys2*SOPT13/100 || ys2>ys1*SOPT13/100)
				return (0);
		}

		// ������ ���μ��� ������ Ȯ���Ѵ�. 
		if (xs1>30 && xs2>30 && ys1>30 && ys2>30)
		{
			ratio1 = xs1*100/ys1;
			ratio2 = xs2*100/ys2;
			if (ratio1>ratio2*SOPT14/100 || ratio2>ratio1*SOPT14/100)
				return (0);
		}

		// ���� ȹ�� ���
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

	// �⺻ ��ǥ�� 0,0���� �����. 
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

	// �⺻ ��ǥ�� 0,0���� �����. 
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

	// ����� 500,250���� ���߾��ش�. 
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

	// �⺻ ��ǥ�� 0,0���� �����. 
	j = 0;
	while (data[j][0]!=-1)
	{
		data[j][0] -= rect_data[0];
		data[j][1] -= rect_data[1];
		j++;
	}

	// ����� 500,250���� ���߾��ش�. 
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

		if (p[i]==10) //10�� ���๮�� \n�̴�.
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
	// ���� ���� ���� 
	if (x1<=x2)
	{
		// �Ʒ��� ���� 
		if (y1<=y2)
		{
			if (1)
//			if (opt==0)
				return (Get_Str(abs(x1-x2),abs(y1-y2),'R','D',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','d',buf));
		}
		// ���� ���� 
		else
		{
			if (1)
//			if (opt==0)
				return (Get_Str(abs(x1-x2),abs(y1-y2),'R','U',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'r','u',buf));
		}
	}
	// ���� ������� 
	else
	{
		// �Ʒ��� ���� 
		if (y1<=y2)
		{
			if (1)
//			if (opt==0)
				return (Get_Str(abs(x1-x2),abs(y1-y2),'L','D',buf));
			else
				return (Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf)+Get_Str(abs(x1-x2),abs(y1-y2),'l','d',buf));
		}
		// ���� ���� 
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
