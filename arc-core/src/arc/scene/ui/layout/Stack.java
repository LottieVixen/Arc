/*******************************************************************************
 * Copyright (c) 2011, Nathan Sweet <nathan.sweet@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package arc.scene.ui.layout;

import arc.struct.Seq;
import arc.struct.SnapshotSeq;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.utils.Layout;

/**
 * A stack is a container that sizes its children to its size and positions them at 0,0 on top of each other.
 * <p>
 * The preferred and min size of the stack is the largest preferred and min size of any children. The max size of the stack is the
 * smallest max size of any children.
 * @author Nathan Sweet
 */
public class Stack extends WidgetGroup{
    private float prefWidth, prefHeight, minWidth, minHeight, maxWidth, maxHeight;
    private boolean sizeInvalid = true;

    public Stack(){
        setTransform(false);
        setWidth(150);
        setHeight(150);
        touchable(Touchable.childrenOnly);
    }

    public Stack(Element... actors){
        this();
        for(Element actor : actors)
            addChild(actor);
    }

    public void invalidate(){
        super.invalidate();
        sizeInvalid = true;
    }

    private void computeSize(){
        sizeInvalid = false;
        prefWidth = 0;
        prefHeight = 0;
        minWidth = 0;
        minHeight = 0;
        maxWidth = 0;
        maxHeight = 0;
        SnapshotSeq<Element> children = getChildren();
        for(int i = 0, n = children.size; i < n; i++){
            Element child = children.get(i);
            float childMaxWidth, childMaxHeight;
            if(child instanceof Layout){
                prefWidth = Math.max(prefWidth, ((Layout)child).getPrefWidth());
                prefHeight = Math.max(prefHeight, ((Layout)child).getPrefHeight());
                minWidth = Math.max(minWidth, ((Layout)child).getMinWidth());
                minHeight = Math.max(minHeight, ((Layout)child).getMinHeight());
                childMaxWidth = ((Layout)child).getMaxWidth();
                childMaxHeight = ((Layout)child).getMaxHeight();
            }else{
                prefWidth = Math.max(prefWidth, child.getWidth());
                prefHeight = Math.max(prefHeight, child.getHeight());
                minWidth = Math.max(minWidth, child.getWidth());
                minHeight = Math.max(minHeight, child.getHeight());
                childMaxWidth = 0;
                childMaxHeight = 0;
            }
            if(childMaxWidth > 0) maxWidth = maxWidth == 0 ? childMaxWidth : Math.min(maxWidth, childMaxWidth);
            if(childMaxHeight > 0) maxHeight = maxHeight == 0 ? childMaxHeight : Math.min(maxHeight, childMaxHeight);
        }
    }

    public void add(Element actor){
        addChild(actor);
    }

    public void layout(){
        if(sizeInvalid) computeSize();
        float width = getWidth(), height = getHeight();
        Seq<Element> children = getChildren();
        for(int i = 0, n = children.size; i < n; i++){
            Element child = children.get(i);
            child.setBounds(0, 0, width, height);
            if(child instanceof Layout) child.validate();
        }
    }

    public float getPrefWidth(){
        if(sizeInvalid) computeSize();
        return prefWidth;
    }

    public float getPrefHeight(){
        if(sizeInvalid) computeSize();
        return prefHeight;
    }

    public float getMinWidth(){
        if(sizeInvalid) computeSize();
        return minWidth;
    }

    public float getMinHeight(){
        if(sizeInvalid) computeSize();
        return minHeight;
    }

}
