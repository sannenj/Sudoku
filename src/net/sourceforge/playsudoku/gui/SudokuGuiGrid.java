/* SPanel created on 08.07.2006 */
package net.sourceforge.playsudoku.gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import net.sourceforge.playsudoku.GV;
import net.sourceforge.playsudoku.SudokuGrid;
import net.sourceforge.playsudoku.SudokuObserver;
import net.sourceforge.playsudoku.UndoRedoStack;
import net.sourceforge.playsudoku.GV.NumberEntry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SudokuGuiGrid extends JPanel implements SudokuObserver {
    
    private SudokuGrid sgrid;

    private SudokuGuiCell[][] cells;
    
    private SudokuGuiCell lastCell;
    
    private Dimension dimCell;
    
    private UndoRedoStack uRS;

    private boolean helpingLines; //JV
    
    private class SudokuGuiCell extends JPanel
        implements MouseListener, FocusListener {
        
        private Color bg; 
        private Color bgSelected;
        private int sudokuRealGridval;
        private boolean isSelected;
        private boolean isSelectedRightMouseBut;
        private int x;
        private int y;
        
        public SudokuGuiCell(int x, int y, Color bg, Color bgSelected, int realGridVal) {
            this.x = x;
            this.y = y;
            this.bg = bg;
            this.bgSelected = bgSelected;
            
            isSelected = false;
            sudokuRealGridval = realGridVal;
            
            setSize(dimCell);
            setPreferredSize(dimCell);
            setMaximumSize(dimCell);
            
            setBackground(bg);
            setBorder(GV.BORDER_RAISED);
            
            addMouseListener(this);
            setFocusable(false);
            removeFocusListener(this);
        }

        public void paint(Graphics g) {
            super.paint(g);

            boolean isDefault = SudokuGrid.isDefault(sudokuRealGridval);
            
            if(isSelected && isSelectedRightMouseBut  && 
                    SudokuMainFrame.getNumberEntry() == NumberEntry.sctn) {
                g.setColor(Color.BLUE);
                g.drawLine(2,2,2,49);
                g.drawLine(2,2,49,2);
                g.drawLine(2,49,49,49);
                g.drawLine(49,2,49,49);
                g.setColor(bg);
            }
            
            g.setFont(GV.FONT_BIG);
            
            int val = 0;
            if(isDefault) {
                g.setColor(Color.BLACK);
                val = SudokuGrid.getGridVal(sudokuRealGridval);
            } else {
                g.setColor(Color.BLUE);
                val = SudokuGrid.getPuzzleVal(sudokuRealGridval);
            }
            
            g.drawString(val == 0 ? "" : ""+val, GV.FONT_BIG_H, GV.FONT_BIG_W);

            g.setFont(GV.FONT_S);
            
            g.drawString(noteToString(1, isDefault, sudokuRealGridval), GV.W1, GV.H1);
            g.drawString(noteToString(2, isDefault, sudokuRealGridval), GV.W2, GV.H1);
            g.drawString(noteToString(3, isDefault, sudokuRealGridval), GV.W3, GV.H1);
            g.drawString(noteToString(4, isDefault, sudokuRealGridval), GV.W1, GV.H2);
            g.drawString(noteToString(5, isDefault, sudokuRealGridval), GV.W4, GV.H2);
            g.drawString(noteToString(6, isDefault, sudokuRealGridval), GV.W3, GV.H2);
            g.drawString(noteToString(7, isDefault, sudokuRealGridval), GV.W1, GV.H3);
            g.drawString(noteToString(8, isDefault, sudokuRealGridval), GV.W2, GV.H3);
            g.drawString(noteToString(9, isDefault, sudokuRealGridval), GV.W3, GV.H3);
            
            if(isDefault) {
                setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                isSelected = false;
            }
            //System.out.println("PAINT X: " + gs.getX() +" Y: " + gs.getY());
        }
        
        private String noteToString(int val, boolean isDefault, int realGridVal) {
            if(SudokuGrid.isDefault(realGridVal) || !SudokuGrid.getNote(realGridVal, val)) {
                return "";
            } 
            return  "" + val;
        }
        
        protected void setRealGridVal(int val) {
            sudokuRealGridval = val;
        }
        
        protected boolean isSelected() {
            return isSelected;
        }
        
        protected boolean isSelectedRightMouseButton() {
            return isSelectedRightMouseBut;
        }

        public void mousePressed(MouseEvent arg0) {}

        public void mouseReleased(MouseEvent arg0) {}

        public void mouseEntered(MouseEvent arg0) {}

        public void mouseExited(MouseEvent arg0) {}
        
        public void mouseClicked(MouseEvent me) {
            
            if (me.getButton() == MouseEvent.BUTTON2) { //JV edit
                isSelectedRightMouseBut = false;               
                if (lastCell.equals(this) && isSelected) {
                    lastCell = this;
                    resetBackGroundColor();
                    this.focusLost(null);   
                } else {
                    lastCell.resetBackGroundColor();                   
                    lastCell.focusLost(null);
                    this.focusGained(null);
                    setBackGroundColor();
                    lastCell = this;
                } //END JV edit
            }else{
                lastCell.focusLost(null);
                lastCell.resetBackGroundColor();
                this.focusGained(null);
                this.setBackGroundColor();
                lastCell = this;
                
                if(me.getButton() == MouseEvent.BUTTON1) {
                    isSelectedRightMouseBut = false;
                } else if(me.getButton() == MouseEvent.BUTTON3) {
                    isSelectedRightMouseBut = true;
                }
                
                if(SudokuMainFrame.getNumberEntry() == NumberEntry.sntc) {
                    doNumberEntry();
                }
            }
        }
        
        private void doNumberEntry() {
            int i = SudokuMainFrame.getLastButton().getVal();
            int oldVal = sgrid.getRealGridVal(x,y);
            if(isSelectedRightMouseBut) {
                if(i == 0)
                    sgrid.deleteAllNotes(x,y);
                else sgrid.setNote(x,y,i);
            } else {
                sgrid.setPuzzleVal(x,y,i);
            }
            int newVal = sgrid.getRealGridVal(x,y);
            uRS.push(oldVal, newVal);
        }

        public void focusGained(FocusEvent arg0) {
            if(SudokuGrid.isEditable(sudokuRealGridval)) {
                isSelected = true;
                setBorder(GV.BORDER_LOWERED);
            }
        }

        public void focusLost(FocusEvent arg0) {
            isSelected = false;
            setBorder(GV.BORDER_RAISED);
        }
        
        
        private void setBackGroundColor(){ // JV
            if (helpingLines) {
                setBGColor(false);
            }            
        }        

        private void resetBackGroundColor() { // JV
            if (helpingLines) {
                setBGColor(true);
            }
        }
        
        private void setBGColor(boolean reset) {
            for (int i = 0; i < 9; i++) {

                cells[this.x][i].setBackground((reset ? cells[this.x][i].bg : cells[this.x][i].bgSelected));
                cells[i][this.y].setBackground((reset ? cells[i][this.y].bg : cells[i][this.y].bgSelected));
            }

            int n = 3*(x/3)+(y/3);
            
            //SELECT 3X3 SQUARE FOR X, Y
            for (int i = 3*(n % 3); i <= 3*(n % 3)+2; i++) {
                for (int j = 3*(n / 3); j <= 3*(n / 3)+2; j++) {
                    cells[j][i].setBackground((reset ? cells[j][i].bg : cells[j][i].bgSelected));
                }
            }
        }
    }

    public SudokuGuiGrid (SudokuGrid grid, UndoRedoStack uRS) {
        this.sgrid = grid;
        this.dimCell = new Dimension(52, 52);
        this.uRS = uRS;
        
        setLayout(new GridLayout(9, 9));
        cells = new SudokuGuiCell[9][9];
        sgrid.addObserver(this);

        boolean b1 = false;
        boolean b2 = false;
        for(int i = 0; i < 9; i++) {
           
            b1 = (i / 3 == 1) ? true : false;
            for(int j = 0; j < 9; j++) {
                b2 = (j / 3 == 1) ? true : false;
                if(b1 && b2) {
                    cells[j][i] = new SudokuGuiCell(j, i, GV.GRID_COLOR_2, GV.GRID_COLOR_2_SELC, grid.getRealGridVal(j,i));
                } else if(b1 && !b2) {
                    cells[j][i] = new SudokuGuiCell(j, i, GV.GRID_COLOR_1, GV.GRID_COLOR_1_SELC, grid.getRealGridVal(j,i));
                }else if(!b1 && b2) {
                    cells[j][i] = new SudokuGuiCell(j, i, GV.GRID_COLOR_1, GV.GRID_COLOR_1_SELC, grid.getRealGridVal(j,i));
                } else{
                    cells[j][i] = new SudokuGuiCell(j, i, GV.GRID_COLOR_2, GV.GRID_COLOR_2_SELC, grid.getRealGridVal(j,i));
                }
                add(cells[j][i]);
            }
        }
        lastCell = cells[0][0];
    }
    
    protected void buttonDownPushed() {
        if(SudokuMainFrame.getNumberEntry() == NumberEntry.sctn) {
            lastCell.doNumberEntry();
        } 
    }

    public void updateCellChange(int cell) {
        int xadr = SudokuGrid.getX(cell);
        int yadr = SudokuGrid.getY(cell);
        cells[xadr][yadr].setRealGridVal(sgrid.getRealGridVal(xadr,yadr));
        cells[xadr][yadr].repaint();
        //System.out.println("SudokuGuiGrid.updateCellChanged: x=" + xadr + ", y=" + yadr);
    }

    public void enableHelpingLines() { // JV
        this.helpingLines = true;
    }

    
    public void disableHelpingLines() { // JV
        lastCell.resetBackGroundColor();
        this.helpingLines = false;
    }
    
    public boolean getHelpingLines() { // JV
        return this.helpingLines;
    }
}