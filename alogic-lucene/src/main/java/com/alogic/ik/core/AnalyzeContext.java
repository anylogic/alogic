package com.alogic.ik.core;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import com.alogic.ik.dic.Dictionary;

/**
 * 分析上下文
 * 
 * @author yyduan
 * @since 1.6.11.32
 */
class AnalyzeContext {
	
	//默认缓冲区大小
	private static final int BUFF_SIZE = 3072;
	//缓冲区耗尽的临界值
	private static final int BUFF_EXHAUST_CRITICAL = 48;	
	
 
	//字符窜读取缓冲
    private char[] segmentBuff;
    //字符类型数组
    private int[] charTypes;
    
    
    //记录Reader内已分析的字串总长度
    //在分多段分析词元时，该变量累计当前的segmentBuff相对于reader起始位置的位移
	private int buffOffset;	
    //当前缓冲区位置指针
    private int cursor;
    //最近一次读入的,可处理的字串长度
	private int available;

	
	//子分词器锁
    //该集合非空，说明有子分词器在占用segmentBuff
    private Set<String> buffLocker;
    
    //原始分词结果集合，未经歧义处理
    private QuickSortSet orgLexemes;    
    //LexemePath位置索引表
    private Map<Integer , LexemePath> pathMap;    
    //最终分词结果集
    private LinkedList<Lexeme> results;
    
	//分词器配置项
	private Dictionary dic;
    
	private boolean smartMode = true;
	
    public AnalyzeContext(Dictionary dic,boolean smartMode){
    	this.dic = dic;
    	this.smartMode = smartMode;
    	this.segmentBuff = new char[BUFF_SIZE];
    	this.charTypes = new int[BUFF_SIZE];
    	this.buffLocker = new HashSet<String>();
    	this.orgLexemes = new QuickSortSet();
    	this.pathMap = new HashMap<Integer , LexemePath>();    	
    	this.results = new LinkedList<Lexeme>();
    }
    
    int getCursor(){
    	return this.cursor;
    }

    char[] getSegmentBuff(){
    	return this.segmentBuff;
    }
    
    char getCurrentChar(){
    	return this.segmentBuff[this.cursor];
    }
    
    int getCurrentCharType(){
    	return this.charTypes[this.cursor];
    }
    
    int getBufferOffset(){
    	return this.buffOffset;
    }
	
    int fillBuffer(Reader reader) throws IOException{
    	int readCount = 0;
    	if(this.buffOffset == 0){
    		//首次读取reader
    		readCount = reader.read(segmentBuff);
    	}else{
    		int offset = this.available - this.cursor;
    		if(offset > 0){
    			//最近一次读取的>最近一次处理的，将未处理的字串拷贝到segmentBuff头部
    			System.arraycopy(this.segmentBuff , this.cursor , this.segmentBuff , 0 , offset);
    			readCount = offset;
    		}
    		//继续读取reader ，以onceReadIn - onceAnalyzed为起始位置，继续填充segmentBuff剩余的部分
    		readCount += reader.read(this.segmentBuff , offset , BUFF_SIZE - offset);
    	}            	
    	//记录最后一次从Reader中读入的可用字符长度
    	this.available = readCount;
    	//重置当前指针
    	this.cursor = 0;
    	return readCount;
    }

    void initCursor(){
    	this.cursor = 0;
    	this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
    	this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
    }
    
    boolean moveCursor(){
    	if(this.cursor < this.available - 1){
    		this.cursor++;
        	this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
        	this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
    		return true;
    	}else{
    		return false;
    	}
    }
	
	void lockBuffer(String segmenterName){
		this.buffLocker.add(segmenterName);
	}
	
	void unlockBuffer(String segmenterName){
		this.buffLocker.remove(segmenterName);
	}
	
	boolean isBufferLocked(){
		return this.buffLocker.size() > 0;
	}

	boolean isBufferConsumed(){
		return this.cursor == this.available - 1;
	}
	
	boolean needRefillBuffer(){
		return this.available == BUFF_SIZE 
			&& this.cursor < this.available - 1   
			&& this.cursor  > this.available - BUFF_EXHAUST_CRITICAL
			&& !this.isBufferLocked();
	}
	
	void markBufferOffset(){
		this.buffOffset += this.cursor;
	}
	
	void addLexeme(Lexeme lexeme){
		this.orgLexemes.addLexeme(lexeme);
	}
	
	void addLexemePath(LexemePath path){
		if(path != null){
			this.pathMap.put(path.getPathBegin(), path);
		}
	}
	
	
	QuickSortSet getOrgLexemes(){
		return this.orgLexemes;
	}
	
	void processUnkownCJKChar(){
		int index = 0;
		for( ; index < this.available ;){
			//跳过标点符号等字符
			if(CharacterUtil.CHAR_USELESS == this.charTypes[index]){
				index++;
				continue;
			}
			//从pathMap找出对应index位置的LexemePath
			LexemePath path = this.pathMap.get(index);
			if(path != null){
				//输出LexemePath中的lexeme到results集合
				Lexeme l = path.pollFirst();
				while(l != null){
					this.results.add(l);
					//将index移至lexeme后
					index = l.getBegin() + l.getLength();					
					l = path.pollFirst();
					if(l != null){
						//输出path内部，词元间遗漏的单字
						for(;index < l.getBegin();index++){
							this.outputSingleCJK(index);
						}
					}
				}
			}else{//pathMap中找不到index对应的LexemePath
				//单字输出
				this.outputSingleCJK(index);
				index++;
			}
		}
		//清空当前的Map
		this.pathMap.clear();
	}
	
	private void outputSingleCJK(int index){
		if(CharacterUtil.CHAR_CHINESE == this.charTypes[index]){			
			Lexeme singleCharLexeme = new Lexeme(this.buffOffset , index , 1 , Lexeme.TYPE_CNCHAR);
			this.results.add(singleCharLexeme);
		}else if(CharacterUtil.CHAR_OTHER_CJK == this.charTypes[index]){
			Lexeme singleCharLexeme = new Lexeme(this.buffOffset , index , 1 , Lexeme.TYPE_OTHER_CJK);
			this.results.add(singleCharLexeme);
		}
	}
		
	boolean hasNextResult(){
		return !this.results.isEmpty();
	}
	
	Lexeme getNextLexeme(){
		//从结果集取出，并移除第一个Lexme
		Lexeme result = this.results.pollFirst();
		while(result != null){
    		//数量词合并
    		this.compound(result);
    		if(dic.isStopWord(this.segmentBuff ,  result.getBegin() , result.getLength())){
       			//是停止词继续取列表的下一个
    			result = this.results.pollFirst(); 				
    		}else{
	 			//不是停止词, 生成lexeme的词元文本,输出
	    		result.setLexemeText(String.valueOf(segmentBuff , result.getBegin() , result.getLength()));
	    		break;
    		}
		}
		return result;
	}
	
	void reset(){
		this.buffLocker.clear();
        this.orgLexemes = new QuickSortSet();
        this.available =0;
        this.buffOffset = 0;
    	this.charTypes = new int[BUFF_SIZE];
    	this.cursor = 0;
    	this.results.clear();
    	this.segmentBuff = new char[BUFF_SIZE];
    	this.pathMap.clear();
	}
	
	private void compound(Lexeme result){
		if(!this.smartMode){
			return ;
		}
   		//数量词合并处理
		if(!this.results.isEmpty()){

			if(Lexeme.TYPE_ARABIC == result.getLexemeType()){
				Lexeme nextLexeme = this.results.peekFirst();
				boolean appendOk = false;
				if(Lexeme.TYPE_CNUM == nextLexeme.getLexemeType()){
					//合并英文数词+中文数词
					appendOk = result.append(nextLexeme, Lexeme.TYPE_CNUM);
				}else if(Lexeme.TYPE_COUNT == nextLexeme.getLexemeType()){
					//合并英文数词+中文量词
					appendOk = result.append(nextLexeme, Lexeme.TYPE_CQUAN);
				}
				if(appendOk){
					//弹出
					this.results.pollFirst(); 
				}
			}
			
			//可能存在第二轮合并
			if(Lexeme.TYPE_CNUM == result.getLexemeType() && !this.results.isEmpty()){
				Lexeme nextLexeme = this.results.peekFirst();
				boolean appendOk = false;
				 if(Lexeme.TYPE_COUNT == nextLexeme.getLexemeType()){
					 //合并中文数词+中文量词
 					appendOk = result.append(nextLexeme, Lexeme.TYPE_CQUAN);
 				}  
				if(appendOk){
					//弹出
					this.results.pollFirst();   				
				}
			}

		}
	}
	
}
