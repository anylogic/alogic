/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 */
package com.alogic.ik.core;

import com.alogic.ik.dic.Dictionary;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * IK分词器主类
 * 
 * @since 1.6.11.32
 */
public final class IKSegmenter {

    //字符窜reader
    private Reader input;
    //分词器上下文
    private AnalyzeContext context;
    //分词处理器列表
    private List<ISegmenter> segmenters;
    //分词歧义裁决器
    private IKArbitrator arbitrator;

    private Dictionary dic;
    private boolean smartMode = true;
    
    public IKSegmenter(Reader input, Dictionary dic,boolean smartMode) {
        this.input = input;
        this.dic = dic;
        this.smartMode = smartMode;
        //初始化分词上下文
        this.context = new AnalyzeContext(dic,smartMode);
        //加载子分词器
        this.segmenters = this.loadSegmenters();
        //加载歧义裁决器
        this.arbitrator = new IKArbitrator();
    }


    private List<ISegmenter> loadSegmenters() {
        List<ISegmenter> segmenters = new ArrayList<ISegmenter>(4);
        //处理字母的子分词器
        segmenters.add(new LetterSegmenter());
        //处理中文数量词的子分词器
        segmenters.add(new CN_QuantifierSegmenter(dic));
        //处理中文词的子分词器
        segmenters.add(new CJKSegmenter(dic));
        return segmenters;
    }

    public synchronized Lexeme next() throws IOException {
        if (this.context.hasNextResult()) {
            //存在尚未输出的分词结果
            return this.context.getNextLexeme();
        } else {
            /*
			 * 从reader中读取数据，填充buffer
			 * 如果reader是分次读入buffer的，那么buffer要进行移位处理
			 * 移位处理上次读入的但未处理的数据
			 */
            int available = context.fillBuffer(this.input);
            if (available <= 0) {
                //reader已经读完
                context.reset();
                return null;

            } else {
                //初始化指针
                context.initCursor();
                do {
                    //遍历子分词器
                    for (ISegmenter segmenter : segmenters) {
                        segmenter.analyze(context);
                    }
                    //字符缓冲区接近读完，需要读入新的字符
                    if (context.needRefillBuffer()) {
                        break;
                    }
                    //向前移动指针
                } while (context.moveCursor());
                //重置子分词器，为下轮循环进行初始化
                for (ISegmenter segmenter : segmenters) {
                    segmenter.reset();
                }
            }
            //对分词进行歧义处理
            this.arbitrator.process(context, this.smartMode);
            //处理未切分CJK字符
            context.processUnkownCJKChar();
            //记录本次分词的缓冲区位移
            context.markBufferOffset();
            //输出词元
            if (this.context.hasNextResult()) {
                return this.context.getNextLexeme();
            }
            return null;
        }
    }

    public synchronized void reset(Reader input) {
        this.input = input;
        context.reset();
        for (ISegmenter segmenter : segmenters) {
            segmenter.reset();
        }
    }
}
