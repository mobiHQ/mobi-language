package mobi.language;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import mobi.core.Mobi;
import mobi.core.common.Relation;
import mobi.core.concept.Attribute;
import mobi.core.concept.AttributeTypeEnum;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;
import mobi.core.concept.Tale;
import mobi.core.relation.CompositionRelation;
import mobi.core.relation.EquivalenceRelation;
import mobi.core.relation.GenericRelation;
import mobi.core.relation.InheritanceRelation;
import mobi.core.relation.SymmetricRelation;
import mobi.exception.ExceptionURI;

public class CreateDomain extends Expression {
  
	private Mobi mobi;
	private Class classA;
	private Class classB;
	private Instance instanceA;
	private Instance instanceB;
	private GenericRelation genericRelation;
	private String typeNameRelation;
	private String propertyA;
	private String propertyB;
	private Tale tale;
	private String nameTaleRelation;
	
	public CreateDomain(String nameDomain) {
		this.mobi = new Mobi(nameDomain);
		this.genericRelation = new GenericRelation();
	}
	
	public Mobi populatedDomain() throws Exception {
		return this.mobi;
	}
	
	public Class createClass(String uri) throws Exception {
		Class class1;
		 if(mobi.getAllClasses().containsKey(uri)){
			 class1 = this.mobi.getClass(uri);
		 } else {
			 class1 = new Class(uri);
			 this.mobi.addConcept(class1);
		 }
		 return class1;
	}
	
	public void createClassA(String uri)throws Exception {
		 if(mobi.getAllClasses().containsKey(uri)){
			 this.classA = this.mobi.getClass(uri);
		 }else{
			 this.classA = new Class(uri);
			 this.mobi.addConcept(this.classA);
		 }
	}
	
	public void createClassB(String uri)throws Exception {
		 if(this.mobi.getAllClasses().containsKey(uri)){
			 this.classB = this.mobi.getClass(uri);
		 }else{
			 this.classB = new Class(uri);
			 this.mobi.addConcept(this.classB);
		 }
	}
	
	public Instance createInstance(String uri) throws Exception {
		Instance instance = null;
		
		if(!uri.equals("-")){
			if(this.mobi.getAllInstances().containsKey(uri)){
				instance = this.mobi.getInstance(uri);
			}else{
				instance = new Instance(uri);
				this.mobi.addConcept(instance);
			}
		}
		
		return instance;
			
	}
	
	public void createInstanceA(String uri) throws Exception {
		if(!uri.equals("-")){
			if(this.mobi.getAllInstances().containsKey(uri)){
				this.instanceA = this.mobi.getInstance(uri);
			}else{
				this.instanceA = new Instance(uri);
				this.mobi.addConcept(instanceA);
			}
		}else{
			this.instanceA = null;
		}
			
	}
	
	public void createInstanceB(String uri) throws Exception {
		if(!uri.equals("-")){
			if(this.mobi.getAllInstances().containsKey(uri)){
				this.instanceB = this.mobi.getInstance(uri);
			}else{
				this.instanceB = new Instance(uri);
				this.mobi.addConcept(instanceB);
			}
		}else{
			this.instanceB = null;
		}
	}
	
	public void createIsOneOf(Instance instance, Class class1) throws ExceptionURI {
		if((this.mobi.getAllInstanceClassRelation().get(instance.getUri()) == null) 
    		||(this.mobi.getAllClassInstanceRelation().get(class1.getUri())== null)
    			|| (!this.mobi.getAllClassInstanceRelation().get(class1.getUri()).contains(instance))) { 
			this.mobi.isOneOf(instance, class1);
		}
	}
	
	/*
     * Condi��o verifica se a instancia e a classe existem ou se uma instancia j� est� contida em uma classe. 
     */
	public void createIsOneOfA(String uri) throws ExceptionURI{
		 if((this.mobi.getAllInstanceClassRelation().get(uri) == null) 
		    		||(this.mobi.getAllClassInstanceRelation().get(this.classA.getUri())== null)
		    			|| (!this.mobi.getAllClassInstanceRelation().get(this.classA.getUri()).contains(this.instanceA))){ 
				this.mobi.isOneOf(this.instanceA, this.classA);
			}
	}
	
	public void createIsOneOfB(String uri) throws ExceptionURI{
		 if((this.mobi.getAllInstanceClassRelation().get(uri) == null) 
		    		||(this.mobi.getAllClassInstanceRelation().get(this.classB.getUri())== null)
		    			|| (!this.mobi.getAllClassInstanceRelation().get(this.classB.getUri()).contains(this.instanceB))){ 
				this.mobi.isOneOf(this.instanceB, this.classB);
			}
	}
	
	
	public void createRelation(String relationType, String frontRelationName, String backRelationName){
		if(relationType.equals("COMPOSITION")){
			if(backRelationName != null){
				mobi.createBidirecionalCompositionRelationship(frontRelationName,backRelationName);
			} else {
				mobi.createUnidirecionalCompositionRelationship(frontRelationName);
			}
		} else if(relationType.equals("INHERITANCE")){
			mobi.createInheritanceRelation(frontRelationName);
		} else if(relationType.equals("EQUIVALENCE")){
			mobi.createEquivalenceRelation(frontRelationName);
		} else if(relationType.equals("SYMMETRIC")){
			mobi.createSymmetricRelation(frontRelationName);
		}		
	}
	
	
    public void addTypeRelation() throws Exception{
    	if(this.typeNameRelation != null){
    		if (this.propertyB != null){
    				CompositionRelation composition = (CompositionRelation)this.mobi.convertToBidirecionalCompositionRelationship(this.genericRelation, this.propertyA, this.propertyB);
    				this.addTaleRelation(this.nameTaleRelation, composition);
    				
    				composition.processCardinality();
    				this.mobi.addConcept(composition);   
    		}else{
    			if(this.typeNameRelation.equals("INHERITANCE")){
    				InheritanceRelation inheritanceRelation = (InheritanceRelation)this.mobi.convertToInheritanceRelation(this.genericRelation, this.propertyA);
    				this.addTaleRelation(this.nameTaleRelation, inheritanceRelation);
    				
    				inheritanceRelation.processCardinality();
    				this.mobi.addConcept(inheritanceRelation);
    				
    			}else if (this.typeNameRelation.equals("EQUIVALENCE")){
        			EquivalenceRelation equivalenceRelation = (EquivalenceRelation)this.mobi.convertToEquivalenceRelation(this.genericRelation, this.propertyA); 
        			this.addTaleRelation(this.nameTaleRelation, equivalenceRelation);
        			
        			equivalenceRelation.processCardinality();
        			this.mobi.addConcept(equivalenceRelation);   
        		
    			}else if (this.typeNameRelation.equals("SYMMETRIC")){
        			SymmetricRelation symmetric = (SymmetricRelation)this.mobi.convertToSymmetricRelation(this.genericRelation, this.propertyA);
        			this.addTaleRelation(this.nameTaleRelation, symmetric);
        			
        			symmetric.processCardinality();
        			this.mobi.addConcept(symmetric);
        		
    			}else{
        			CompositionRelation composition = (CompositionRelation)this.mobi.convertToUnidirecionalCompositionRelationship(this.genericRelation, this.propertyA);
        			this.addTaleRelation(this.nameTaleRelation, composition);
        			composition.processCardinality();
        			this.mobi.addConcept(composition);
        		}
        	}
    	}
    }
    
    public void addGenericRelation(){
    	this.genericRelation.addInstanceRelation(this.instanceA, this.instanceB);
    }
    
    public void addTaleRelation(String uri, Relation relation){
    	if(this.mobi.getAllTales().containsKey(uri)){
    		this.mobi.getAllTales().get(uri).getRelations().add(relation); //Insere a rela��o na hist�ria criada
    	}
    }
    
    /**
     * @author Andr� Schmid
     * Adds the attributes to the given MOBI class name
     * @param classUri				The uri for the MOBI class
     * @param attributeNameAndTypeMap		The map of attributes names and respectives values type to add
     * @throws Exception
     */
    public void createClassAttributes(String classUri, Map<String, AttributeTypeEnum> attributeNameAndTypeMap) throws Exception {
    	Class mobiClass;
    	if(mobi.getAllClasses().containsKey(classUri)){
    		mobiClass = mobi.getAllClasses().get(classUri);	
    	} else {
    		mobiClass = new Class(classUri);
    		mobi.addConcept(mobiClass);
    	}
    	for (String attributeName : attributeNameAndTypeMap.keySet()) {
    		Attribute newAttribute = new Attribute(attributeName, attributeNameAndTypeMap.get(attributeName));
    		mobi.addConcept(newAttribute);
    		mobi.belongsTo(mobiClass, newAttribute);
		}			
    }
    
    /**
     * @author Andr� Schmid
     * Using the given Instances, it adds to the Instances Classes the related Attributes
     * @param instanceAttributeAndValueMap
     * 		[instanceUri, [attributeUri, [value, type](1)]*]*
     * @throws Exception
     * 		if one of the given Instances was not added to MOBI
     * 		if one of the given Instances has no classes associated
     * 		if an attribute is already defined on a Class and it tries to add another attribute with the same name but different type 		
     */
    public void createClassAttributesByInstances(HashMap<String, HashMap<String, HashMap<String, AttributeTypeEnum>>> instanceAttributeAndValueMap) throws Exception {
    	System.out.println("createClassAttributesByInstaces");
    	System.out.println(instanceAttributeAndValueMap);
    	for (String instanceUri : instanceAttributeAndValueMap.keySet()) {
    		Instance mobiInstance = mobi.getAllInstances().get(instanceUri);
    		if(mobiInstance == null){
				throw new Exception("The instance \"" + instanceUri + "\" was not added to MOBI");
			}
			
			Set<Class> instanceClasses = mobi.getAllInstanceClassRelation().get(mobiInstance.getUri());
			if(instanceClasses == null || instanceClasses.size() == 0){
				throw new Exception("The instance \"" + instanceUri + "\" has no classes associated");
			}
			for (Class mobiClass : instanceClasses) {
				HashMap<String, HashMap<String, AttributeTypeEnum>> attributeAndValue = instanceAttributeAndValueMap.get(mobiInstance.getUri());
				System.out.println("Atributos para a inst�ncia " + mobiInstance.getUri() + ", da classe " + mobiClass.getUri() + ": " +  attributeAndValue);
				for (String attributeUri : attributeAndValue.keySet()) {
					HashMap<String, AttributeTypeEnum> valueAndType = attributeAndValue.get(attributeUri);
					String value = "";
					AttributeTypeEnum type = AttributeTypeEnum.STRING;
					for (String value1 : valueAndType.keySet()) {
						 value = value1;
						 type = valueAndType.get(value1);			 
					}
					
					Attribute attribute = new Attribute(attributeUri, type);
					boolean classAlreadyHasAttribute = false;
					for (Attribute attribute1 : mobi.getAllClassAttributes(mobiClass)) {
						if(attribute.getUri().equals(attribute1.getUri())) {
							classAlreadyHasAttribute = true;
							if(attribute.getType() != attribute1.getType()){
								throw new Exception("There is already an attribute \"" + attribute.getUri() + "\" with another type defined on class " + mobiClass.getUri());
							}
						}
					}
					if(!classAlreadyHasAttribute){
						mobi.belongsTo(mobiClass, attribute);	
					}
					mobi.setAttributeValue(mobiInstance, attribute, value);
				}
			}
			
		}
    }
    
	
//    public void palavraReservada(String name){
//    	
//    }
    
	public Mobi getMobi() {
		return mobi;
	}

	public Instance getInstanceA() {
		return instanceA;
	}

	public Instance getInstanceB() {
		return instanceB;
	}

	public Class getClassA() {
		return classA;
	}

	public Class getClassB() {
		return classB;
	}

	public GenericRelation getGenericRelation() {
		return genericRelation;
	}

	public void setGenericRelation(GenericRelation genericRelation) {
		this.genericRelation = genericRelation;
	}

	public String getTypeNameRelation() {
		return typeNameRelation;
	}

	public void setTypeNameRelation(String typeNameRelation) {
		this.typeNameRelation = typeNameRelation;
	}

	public String getPropertyA() {
		return propertyA;
	}

	public void setPropertyA(String propertyA) {
		this.propertyA = propertyA;
	}

	public String getPropertyB() {
		return propertyB;
	}

	public void setPropertyB(String propertyB) {
		this.propertyB = propertyB;
	}

	public Tale getTale() {
		return tale;
	}

	public void setTale(Tale tale) {
		this.tale = tale;
	}

	public String getNameTaleRelation() {
		return nameTaleRelation;
	}

	public void setNameTaleRelation(String nameTaleRelation) {
		this.nameTaleRelation = nameTaleRelation;
		
	}
	
	

}