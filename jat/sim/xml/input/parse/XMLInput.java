/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can 
 * redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software 
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
package jat.sim.xml.input.parse;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.String;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jat.sim.xml.input.*;
import jat.spacecraft.Spacecraft;
import jat.matvec.data.VectorN;

/**
 * @author Richard C. Page III
 *
 */
public class XMLInput { //implements Input {

    private String inputfile;
    protected InputType.InitialStateType type;
    protected InputType.InitialStateType.CartesianType X;
    protected InputType.TimeType T;
    protected InputType.SpacecraftType scparam;
    protected Spacecraft sc;
    
    public XMLInput(){
        inputfile = new String("C:/Code/Jat/jat/xml/input/sim_input.xml");
        parse();
    }
    
    public XMLInput(String in){
        inputfile = in;
        parse();
    }
    
    private void parse(){
        try {
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            JAXBContext jc = JAXBContext.newInstance( "jat.sim.xml.input" );
            
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            
            // unmarshal a po instance document into a tree of Java content
            // objects composed of classes from the primer.po package.
            Input xml = 
                (Input)u.unmarshal( new FileInputStream( inputfile) );
                
            // examine some of the content in the PurchaseOrder
            
            // display the shipping address
            type = xml.getInitialState();
            X = xml.getInitialState().getCartesian();
            T = xml.getTime();
            scparam = xml.getSpacecraft();
            VectorN r0 = new VectorN(X.getX(),X.getY(),X.getZ());
            r0 = r0.times(1000.0);
            VectorN v0 = new VectorN(X.getXdot(),X.getYdot(),X.getZdot());
            v0 = v0.times(1000.0);
            sc = new Spacecraft(r0,v0,scparam.getCR(),scparam.getCd(),scparam.getArea(),scparam.getMass());
            //displayX(X);
            
            // display the items
//            Items items = po.getItems();
//            displayItems( items );
            
        } catch( JAXBException je ) {
            je.printStackTrace();
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    public Spacecraft getSpacecraft(){
        return sc;
    }
    
    public double getMJD(){ return T.getMJD();}
    public double getT0(){ return T.getT0();}
    public double getTf(){ return T.getTf();}
    public double getStepSize(){ return T.getDt();}
    public double getThinning(){ return T.getThinning();}
    
    /* (non-Javadoc)
     * @see jat.sim.xml.input.InputType#getTime()
     */
    public InputType.TimeType getTime() {
        // TODO Auto-generated method stub
        return T;
    }

    /* (non-Javadoc)
     * @see jat.sim.xml.input.InputType#getInitialState()
     */
    public InputType.InitialStateType getInitialState() {
        // TODO Auto-generated method stub
        return type;
    }
    
    public InputType.InitialStateType.CartesianType getInitialStateCartesian(){
        return X;
    }

    
}
