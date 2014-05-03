package mobi.language;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;

import mobi.core.Mobi;
import mobi.core.common.Relation;
import mobi.core.concept.Tale;
import mobi.core.relation.CompositionRelation;
import mobi.core.relation.EquivalenceRelation;
import mobi.core.relation.GenericRelation;
import mobi.core.relation.InheritanceRelation;
import mobi.core.relation.SymmetricRelation;

public class LittleLanguage {
	
	public static void main(String args[]){
		LittleLanguage.carregaDominioLanguage(null, null);
	}
 
	/**
	 * 
	 * @param genericFilePath			.txt file path containing the generic MOBI mapping (without all relationships)
	 * @param relationFilePath			.txt file path containing the complete MOBI mapping (with all relationships)
	 * @return
	 * 		the MOBI generated from the files
	 */
	public static Mobi carregaDominioLanguage(String genericFilePath, String relationFilePath) {
		if(genericFilePath == null){
			genericFilePath = "D:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/LojaVirtualGeneric.txt";
		}
		if(relationFilePath == null){
			relationFilePath = "D:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/LojaVirtualCompleteMonoToCompileWithClass.txt";
		}
		Mobi mobi = null;
	    //File file = new File("C:/mobi/readFileInh.txt");
//	    File file = new File("X:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/Pedido.txt");
//		File file = new File("X:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/Eleicao.txt");
		File file = new File(genericFilePath);
//		File file = new File("X:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/teste.txt");
		FileInputStream fis = null;
		
		Mobi mobi2 = null;
		//File fileTypeRelation = new File ("C:/mobi/readFileTypeRelation.txt");
		//File fileTypeRelation = new File ("C:/mobi/readFileTale.txt");
		//File fileTypeRelation = new File("C:/mobi/readFileInhType.txt");
//		File fileTypeRelation = new File("X:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/Pedido.txt");
		//File fileTypeRelation = new File("C:/mobi/readFileCompTale.txt");
//		File fileTypeRelation = new File("X:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/Eleicao.txt");
		File fileTypeRelation = new File(relationFilePath);
//		File fileTypeRelation = new File("X:/Dropbox/Projeto Final/MOBI/Workspace/mobi Ramon/teste.txt");
		
		FileInputStream fisEspecificRelation = null;
		
		try {
				fis = new FileInputStream(file);
				Parser p = new Parser(fis);
				Expression exp = p.parse();
				mobi = exp.populatedDomain();
				System.out.println("\n ==============================================LOG==========================================================");
				
				int cont = 0; // N�O VAI FICAR AQUI
				Iterator<GenericRelation> it = mobi.getAllGenericRelations().values().iterator();
				while (it.hasNext()){
					cont++;
					GenericRelation genericRelation = it.next();
					Collection<Integer> possibilities = mobi.infereRelation(genericRelation);
					//System.out.println(mobi.getRelationPossibilitiesString(genericRelation));
//					System.out.println("Rela��o " + cont + " dom�nio");
				    if((possibilities.contains(Relation.EQUIVALENCE)) && (possibilities.contains(Relation.INHERITANCE)) ){
				    	System.out.println("A " + cont + "� Rela��o criada pode ser uma Rela��o de Equival�ncia ou uma Rela��o de Heran�a \n");
				    }
				    
//				    System.out.println("Rela��o " + cont + " dom�nio");
				    if ((possibilities.contains(Relation.BIDIRECIONAL_COMPOSITION))
				    		&& (possibilities.contains(Relation.SYMMETRIC_COMPOSITION))
				    		&& (possibilities.contains(Relation.UNIDIRECIONAL_COMPOSITION))){
				    	System.out.println("A " + cont + "� Rela��o criada pode ser uma Rela��o de Composi��o Unidirecional ou uma Rela��o de Composi��o Bidirecional ou uma Rela��o de Composi��o Sim�trica");
				    }
				    
				    System.out.println("Cardinalidade do grupo A � " + genericRelation.getCardinalityA() + " em rela��o ao grupo B");
				    System.out.println("Cardinalidade do grupo B � " + genericRelation.getCardinalityA() + " em rela��o ao grupo A \n");
				    
				   
				}
				
				System.out.println("==============================================LOG==========================================================");
				
				fisEspecificRelation = new FileInputStream(fileTypeRelation);
				ParserSpecificRelation parserEspecificRelation = new ParserSpecificRelation(fisEspecificRelation); 
				Expression expression = parserEspecificRelation.parse();
				mobi2 = expression.populatedDomain();

				System.out.println("==============================================HIST�RIAS DO DOM�NIO==========================================================");
				if(mobi2.getAllTales().size() > 0){
					Iterator<Tale> iterator = mobi2.getAllTales().values().iterator();
					while(iterator.hasNext()){
						Tale tale = iterator.next();
						System.out.println("Hist�ria da rela��o: " + tale.getUri());
						System.out.println("Conte�do da Hist�ria: " + tale.getText());
						System.out.println("Rela��es da Hist�ria " + tale.getUri());
						
						
						Iterator<Relation> itr = tale.getRelations().iterator(); 
						 while(itr.hasNext()){
							 Relation relation = itr.next();
							if(relation instanceof CompositionRelation) {
								CompositionRelation compositionRelation = (CompositionRelation) relation;
								System.out.println("\r Rela��o: " + compositionRelation.getUri());
							
							}else if(relation instanceof EquivalenceRelation){
								EquivalenceRelation equivalenceRelation = (EquivalenceRelation) relation;
								System.out.println("\r Rela��o: " + equivalenceRelation.getUri());
							
							}else if(relation instanceof InheritanceRelation){
								InheritanceRelation inheritanceRelation  = 	(InheritanceRelation) relation;	
								System.out.println("\r Rela��o: " + inheritanceRelation.getUri());
							
							}else{
								SymmetricRelation symmetricRelation = (SymmetricRelation) relation;	
								System.out.println("\r Rela��o: " + symmetricRelation.getUri());
							}
						 }
						 System.out.println();
					}
				}else{
					System.out.println("N�o h� hist�ria no dom�no modelado");
				}
				System.out.println("==============================================HIST�RIAS DO DOM�NIO==========================================================");
				
				System.out.println("==============================================RELA��ES DO DOM�NIO==========================================================");
				cont=0;
				if(mobi2.getAllCompositionRelations().size() > 0){
					//cont++;
					Iterator<CompositionRelation> iterator = mobi2.getAllCompositionRelations().values().iterator();
					while(iterator.hasNext()){
						CompositionRelation composition = iterator.next();
						//System.out.println("A" +cont+ "� Rela��o � uma Rela��o de Composi��o \n");
						System.out.println("Rela��o: " + composition.getUri());
						System.out.println("Rela��o ClasseA: " + composition.getNameA() + " Rela��o ClasseB: " + composition.getNameB());
						System.out.println("Cardinalidade do grupo A � " + composition.getCardinalityA() + " em rela��o ao grupo B");
						System.out.println("Cardinalidade do grupo B � " + composition.getCardinalityB() + " em rela��o ao grupo A \n");
					}
					
				}
				if(mobi2.getAllEquivalenceRelations().size() > 0){
					cont++;
					Iterator<EquivalenceRelation> iterator = mobi2.getAllEquivalenceRelations().values().iterator();
					while(iterator.hasNext()){
						EquivalenceRelation equivalenceRelation = iterator.next();
						System.out.println("Rela��o: " + equivalenceRelation.getUri());
						System.out.println("Rela��o de equival�ncia");
						System.out.println("Cardinalidade do grupo A � " + equivalenceRelation.getCardinalityA() + " em rela��o ao grupo B");
						System.out.println("Cardinalidade do grupo B � " + equivalenceRelation.getCardinalityB() + " em rela��o ao grupo A \n");
					}
					
				}
				
				if(mobi2.getAllInheritanceRelations().size() > 0){
					cont++;
					Iterator<InheritanceRelation> iterator = mobi2.getAllInheritanceRelations().values().iterator();
					while(iterator.hasNext()){
						InheritanceRelation inheritanceRelation = iterator.next();
						System.out.println("Rela��o: " + inheritanceRelation.getUri());
						System.out.println("Rela��o de Heran�a");
						System.out.println("Cardinalidade do grupo A � " + inheritanceRelation.getCardinalityA() + " em rela��o ao grupo B");
						System.out.println("Cardinalidade do grupo B � " + inheritanceRelation.getCardinalityB() + " em rela��o ao grupo A \n");
					}
				}
				
				if(mobi2.getAllSymmetricRelations().size() > 0){
					cont++;
					Iterator<SymmetricRelation> iterator = mobi2.getAllSymmetricRelations().values().iterator();
					while(iterator.hasNext()){
						SymmetricRelation symmetricRelation = iterator.next();
						System.out.println("Rela��o: " + symmetricRelation.getUri());
						System.out.println("Rela��o de Heran�a");
						System.out.println("Cardinalidade do grupo A � " + symmetricRelation.getCardinalityA() + " em rela��o ao grupo B");
						System.out.println("Cardinalidade do grupo B � " + symmetricRelation.getCardinalityB() + " em rela��o ao grupo A \n");
					}
				}
				System.out.println("==============================================RELA��ES DO DOM�NIO==========================================================");
				
	  } catch (Exception e) {
	      e.printStackTrace();
	    
	    }
//	  return mobi;
	  return mobi2;
	}
}
