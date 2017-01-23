#include <cstdlib>
#include <vector>
#include <fstream>
#include <string>
#include <string.h>
#include <ctime>
#include <iostream>
using namespace std;
#define x 'X'
#define o 'O'
#define empty '.'
#define INFINITY 100000000
#define MAX 26
char _board[MAX*MAX + 1];
int score[MAX*MAX];
static char myply, opply;
static int N=0, maxdepth;
class Move
{
public:
	int index;
	int movetype;
};
int maxValue(int depth);
void generateAction(char ply, vector<Move>& actions);
bool checkBoundary(int index);
void makeMove(Move move, char myply);
void copyBoard(char newbd[MAX*MAX + 1], char orgnbd[MAX*MAX + 1], int N);
bool isFull();
Move alphabeta(int depth);
int maxValue(int depth, int alpha, int beta, Move& rs);
int minValue(int depth, int alpha, int beta, Move& rs);
int gameState()
{
	//calculate score
	int mysum = 0, opsum = 0;
	int i = 0;
	for (; i<N*N; i++)
	{
		if (_board[i] == empty)
		{
			continue;
		}
		if (_board[i] == myply)
			mysum += score[i];
		if (_board[i] == opply)
			opsum += score[i];
	}
	return mysum - opsum;
}
inline bool isFull()
{
	int i;
	for (i = 0; i < N*N; i++)
	{
		if (_board[i] == empty)
			return false;
	}
	return true;
}
inline void makeMove(Move move, char ply)
{
	int i, directions[4] = { move.index - N,move.index + N,move.index - 1,move.index + 1 };
	char op = ply == x ? o : x;
	_board[move.index] = ply;
	if (move.movetype == 0) return;//stake
	for (i = 0; i < 4; i++)
	{
		if (checkBoundary(directions[i]))//opponent occupy
		{//conquer
			if(_board[directions[i]] == op)
                _board[directions[i]] = ply;
		}
	}
}
void copyBoard(char newbd[MAX*MAX + 1], char orgnbd[MAX*MAX + 1], int N)
{
	int i;
	for (i = 0; i < N*N; i++)
	{
		newbd[i] = orgnbd[i];
	}
	newbd[i] = '\0';
}
void generateAction(char ply, vector<Move>& actions)
{
	int direction[4] = { 0 };
	Move mv;
	int i, j;
	for (i = 0; i < N*N; i++)
	{
		if (_board[i] == empty)
		{
			mv.index = i;
			mv.movetype = 0;
			actions.push_back(mv);
		}//stake
    }
    for(i=0;i<N*N;i++)
    {
		if (_board[i] == ply)
		{
			direction[0] = i - N;
			direction[1] = i + N;
			direction[2] = i - 1;
			direction[3] = i + 1;
            
			for (j = 0; j < 4; j++)
			{
				if (checkBoundary(direction[j]))
				{
					if (_board[direction[j]] == empty)
					{
						mv.index = direction[j];
						mv.movetype = 1;
						actions.push_back(mv);
					}
				}//raid
			}
		}
	}
}

inline bool checkBoundary(int index)
{
	if (index < 0 || index >= N*N) return false;
	return true;
}
int minValue(int depth)
{
	if (depth <= 0 || isFull())
	{
		return gameState();
	}
    
	int bestValue = +INFINITY, value;//v=+inf
	vector<Move>vec;
	generateAction(opply, vec);
	vector<Move>::iterator it;
	char tempbd[MAX*MAX + 1];
	strlcpy(tempbd,_board,N*N);
    //copyBoard(tempbd, _board,N);
	for (it = vec.begin(); it != vec.end(); ++it)//for each v in actions(state)
	{
		makeMove(*it, opply);
		value = maxValue(depth - 1);//max
		strlcpy(_board,tempbd,N*N);
        //copyBoard(_board,tempbd,N);
		if (value < bestValue)
		{
			bestValue = value;
		}//v = min(v, maxvalue...)
	}
	//if(vec.size()>0)vec.clear();
	return bestValue;//return v
}
int maxValue(int depth)
{
	//if cutoff-test, return eval
	if (depth <= 0 || isFull())
		return gameState();
    
	int bestValue = -INFINITY, value;//v=-inf
	vector<Move> vec;
	generateAction(myply, vec);
	vector<Move>::iterator it;
	//for each v in actions(state)
	char tempbd[MAX*MAX + 1];
	strlcpy(tempbd,_board,N*N);
    //copyBoard(tempbd, _board,N);
	for (it = vec.begin(); it != vec.end(); ++it)
	{
		makeMove(*it, myply);//board change
		value = minValue(depth - 1);//min
		strlcpy(_board,tempbd,N*N);
        //copyBoard(_board,tempbd,N);
        //recover scene, the original state
		if (value > bestValue)
		{
			bestValue = value;
		}//v=max(v,minvalue..)
	}
	//if(vec.size()>0)vec.clear();
	return bestValue;//return v
}
Move minimax(int depth)
{
	int bestValue, value;
	Move bestmove;
	vector<Move> vec;
	generateAction(myply, vec);
	bestValue = -INFINITY;
	vector<Move>::iterator it;
    
	char tempbd[MAX*MAX + 1];
    strlcpy(tempbd,_board,N*N);
	//copyBoard(tempbd,_board,N);
	//for each v in actions(state)
	for (it = vec.begin(); it != vec.end(); ++it)
	{
		makeMove(*it, myply);
		value = minValue(depth - 1);
		strlcpy(_board,tempbd,N*N);
        //copyBoard(_board, tempbd, N);
		if (value > bestValue)
		{
			bestValue = value;
			bestmove.index = it->index;
			bestmove.movetype = it->movetype;
		}//v=max(v,minvalue..)
	}
	//if(vec.size()>0)vec.clear();
	return bestmove;
}
int minValue(int depth, int alpha, int beta, Move& rs)
{
	if (depth <= 0 || isFull())
	{
		return gameState();
	}
	Move bestmv;
	int bestValue = +INFINITY, value;//v=+inf
	vector<Move>vec;
	generateAction(opply, vec);
	vector<Move>::iterator it;
	char tempbd[MAX*MAX + 1];
	strlcpy(tempbd,_board,N*N);
    //copyBoard(tempbd, _board, N);
	for (it = vec.begin(); it != vec.end(); ++it)//for each v in actions(state)
	{
		makeMove(*it, opply);
		value = maxValue(depth - 1, alpha,beta, rs);//max
		strlcpy(_board,tempbd,N*N);
        //copyBoard(_board, tempbd,N);
		
		if (value < bestValue)
		{
			bestValue = value;
			bestmv.index = it->index;
			bestmv.movetype = it->movetype;
		}//v = min(v, maxvalue...)
		if (bestValue <= alpha)
		{
			return bestValue;
		}
		beta = beta < bestValue ? beta : bestValue;
	}
	vec.clear();
	if (depth == maxdepth)
	{
		rs.index = bestmv.index;
		rs.movetype = bestmv.movetype;
	}
	return bestValue;//return v
}
int maxValue(int depth, int alpha, int beta, Move& rs)
{
	//if cutoff-test, return eval
	if (depth <= 0 || isFull())
	{
		return gameState();
	}
    
	int bestValue = -INFINITY, value;//v=-inf
	vector<Move> vec;
	generateAction(myply, vec);
	vector<Move>::iterator it;
	
	//for each v in actions(state)
	char tempbd[MAX*MAX + 1];
	strlcpy(tempbd,_board,N*N);
    //copyBoard(tempbd, _board, N);
	Move bestmv;
	for (it = vec.begin(); it != vec.end(); ++it)
	{
		makeMove(*it,myply);
		value = minValue(depth - 1,alpha,beta, rs);//min
		strlcpy(_board,tempbd,N*N);
        //copyBoard(_board, tempbd, N);
		if (value > bestValue)
		{
			bestValue = value;
			bestmv.index = it->index;
			bestmv.movetype = it->movetype;
		}//v=max(v,minvalue..)
		if (bestValue >= beta)
		{
			return bestValue;
		}
		alpha = alpha > bestValue ? alpha : bestValue;
	}
	vec.clear();
	if (depth == maxdepth)
	{
		rs.index = bestmv.index;
		rs.movetype = bestmv.movetype;
	}
    
	return bestValue;//return v
}
Move alphabeta(int depth)
{
	Move result;
	int alpha = -INFINITY, beta = +INFINITY;
	int bestValue = maxValue(depth, alpha, beta, result);
	return result;
}


int main()
{
    time_t before;
    before = time(NULL);
    ifstream in("input.txt");
    ofstream out("output.txt");
    if (in.eof())
    {
        out.close();
        exit(1);
    }
    string s, mode;
    int row = 0, i;
    Move mv;
    
    in >> N;
    in >> mode;
    in >> myply;
    in >> maxdepth;
    opply = myply == x ? o : x;
    for (i = 0; i < N*N; i++)
    {
        in >> score[i];
    }
    while (in >> s)
    {
        for (i = 0; i < N; i++)
            _board[row*N + i] = s[i];
        row++;
    }
    _board[N*N] = '\0';
    
    in.close();
    if (mode == "MINIMAX")
        mv = minimax(maxdepth);
    if (mode == "ALPHABETA")
        mv = alphabeta(maxdepth);
    if(N!=0)
    {
        char rownum = 'A' + mv.index%N;
        int indx = (mv.index + 1) % N == 0 ? (mv.index + 1) / N : (mv.index + 1) / N + 1;
        string type = mv.movetype == 0 ? "Stake" : "Raid";
    
        makeMove(mv, myply);
        if (out.is_open())
        {
            out << rownum << indx << " " << type;
            for (int i = 0; i < N; ++i)
            {
                out << endl;
                for (int j = 0; j < N; ++j)
                {
                    out << _board[i*N + j];
                }
            }
        }
    }
    time_t after;
    after = time(NULL);
    out << endl<<"time:"<<after - before;
    out.close();
}