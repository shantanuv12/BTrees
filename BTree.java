package col106.a3;
//import java.util.Comparable;
import java.util.List;
import java.util.Vector;
@SuppressWarnings("unchecked")
public class BTree<Key extends Comparable<Key>,Value> implements DuplicateBTree<Key,Value> {
    private int n;
    private static int M;
	private int height;
    private BTNode<Key,Value> root;
    private static String traversal;
    private static List<Object> list;
    private static int occur;
	private static class Node<Key,Value>{    // creating a node for storing key-value pairs
        //private Node next;
        private Comparable<Key> key;
        private Value value;
        private Node(Comparable<Key> key, Value value){
            this.key=key;
            this.value=value;
            //this.next=next;
        }
        private Node(){
            this.key=null;
            this.value=null;
        }
        /*Node(){
            this.key=null;
            this.value=null;
            this.next=null;
        }*/
    }
    private static class BTNode<Key,Value>{
        //private int t;
        private boolean leaf;
        private int keys;      //No. of children
        private Node<Key,Value>[] arr;    //array of nodes
        private BTNode<Key,Value>[] child; //array of children
        private BTNode(){
            keys=0;
        }
        private BTNode(boolean l){
            keys=0;
            leaf=l;
            arr=new Node[M-1];
            child=new BTNode[M];
            for(int i=0;i<M;i++){
                if(i==M-1){
                child[i]=new BTNode<Key,Value>();
            }
                else{
                    arr[i]=new Node<Key,Value>();
                    child[i]=new BTNode<Key,Value>();
                }
            } 
        }
        private void travel(){
            int i;
            traversal+="[";
            for(i=0;i<keys;i++){
                if(leaf==false)
                {
                    child[i].travel();
                    traversal+=arr[i].key+"="+arr[i].value+",";
                }
                else {
                	if(leaf==true && i==keys-1)    
                		traversal+=arr[i].key+"="+arr[i].value;
                	else{
                		traversal+=arr[i].key+"="+arr[i].value+",";
                	}
                }
            }
            //traversal+="],";
            if(leaf==false){
                child[i].travel();
            }
            if(leaf==false) traversal=traversal.substring(0, traversal.length()-1);
            traversal+="],";
        }
        private void search(Comparable<Key> key){
            //list=new Vector<>();
            int i;
            for(i=0;i<keys;i++){
                if(leaf==false)
                    child[i].search(key);
                if(equalto(key, arr[i].key)) 
                    list.add(arr[i].value);    
            }
            if(leaf==false){
                child[i].search(key);
            }
            //return list;
        }
        private void occur(Comparable<Key> key){
            //list=new Vector<>();
            int i;
            for(i=0;i<keys;i++){
                if(leaf==false)
                    child[i].occur(key);
                if(equalto(key, arr[i].key)) 
                    occur++;    
            }
            if(leaf==false){
                child[i].occur(key);
            }
            //return list;
        }
        private boolean equalto(Comparable<Key> key1,Comparable<Key> key2){
            return key1.compareTo((Key)key2)==0;
        }
        private boolean lessthan(Comparable<Key> key1,Comparable<Key> key2){
            return key1.compareTo((Key)key2)<0;
        }
        private void insertnonfull(Comparable<Key> key, Value value){
            int i=keys-1;
            //System.out.println(i+" :i  keys: " +keys);
        if(leaf==true){
            while(i>=0 && lessthan(key,arr[i].key)){
                arr[i+1].key=arr[i].key;
                arr[i+1].value=arr[i].value;
                i--;
            }  
            
            arr[i+1].key=key;
            arr[i+1].value=value;
            
            keys+=1;
            //System.out.println(" arr[i+1]: "+arr[i+1].key+" = "+arr[i+1].value);
        }
        else{
            while(i>=0 && lessthan(key,arr[i].key)){
                i--;
            }
            if(child[i+1].keys==M-1){
                childsplit(i+1,child[i+1]);
            if(lessthan(arr[i+1].key,key)) i++;
            }
            child[i+1].insertnonfull(key, value);
        }
    }
        
    private void childsplit(int i,BTNode<Key,Value> x){
        BTNode<Key,Value> z= new BTNode<Key,Value>(x.leaf);
        z.keys=(M/2)-1;
        for(int j=0;j<(M/2-1);j++){
            z.arr[j].key= x.arr[j+(M/2)].key;
            z.arr[j].value= x.arr[j+(M/2)].value;
        }
        if(x.leaf==false){
            for(int j=0;j<M/2;j++){
                z.child[j]=x.child[j+M/2];
            }
        }
        x.keys=M/2-1;
        for(int k=keys;k>=i+1;k--){
            child[k+1]=child[k];
        }
        child[i+1]=z;
        for(int j=keys-1;j>=i;j--){
            arr[j+1].key=arr[j].key;
            arr[j+1].value=arr[j].value;
        }
        arr[i].key=x.arr[(M/2)-1].key;
        arr[i].value=x.arr[(M/2)-1].value;
        keys+=1;
    }
    private void merge(int ind){
        BTNode<Key,Value> ch= this.child[ind];
        BTNode<Key,Value> sib=this.child[ind+1];

        ch.arr[M/2-1].key= arr[ind].key;
        ch.arr[M/2-1].value=arr[ind].value;

        for(int i=0;i<sib.keys;++i){
            ch.arr[i+M/2].key=sib.arr[i].key;
            ch.arr[i+M/2].value=sib.arr[i].value;
        }
        if(ch.leaf==false){
            for(int i=0;i<=sib.keys;++i){
                ch.child[i+M/2]=sib.child[i];
            }
        }
        for(int i=ind+1;i<keys;++i){
            arr[i-1].key=arr[i].key;
            arr[i-1].value=arr[i].value;
            }
        for(int i=ind+2;i<=keys;++i){
            child[i-1]=child[i];
            }

        ch.keys=ch.keys+sib.keys+1;
        //this.child[ind+1]=null;
        keys--;
        return;
        }
    private void borrowFromnext(int ind){
        BTNode<Key,Value> ch=this.child[ind];
        BTNode<Key,Value> sib=this.child[ind+1];
        ch.arr[ch.keys].key=arr[ind].key;
        ch.arr[ch.keys].value=arr[ind].value;
        if(ch.leaf==false){
            ch.child[ch.keys+1]=sib.child[0];
        }
        arr[ind].key=sib.arr[0].key;
        arr[ind].value=sib.arr[0].value;

        for(int i=1;i<sib.keys;++i){
            sib.arr[i-1].key=sib.arr[i].key;
            sib.arr[i-1].value=sib.arr[i].value;         
          }
        if(sib.leaf==false){
            for(int i=1;i<=sib.keys;++i){
                sib.child[i-1]=sib.child[i];
            }
        }
        ch.keys+=1;
        sib.keys-=1;
        return;
    }    
    private void borrowFromprev(int ind){
        BTNode<Key,Value> ch=this.child[ind];
        BTNode<Key,Value> sib=this.child[ind-1];
        for(int i=ch.keys-1;i>=0;--i){
            ch.arr[i+1].key=ch.arr[i].key;
            ch.arr[i+1].value=ch.arr[i].value;
        }
        if(ch.leaf==false){
            for(int i=ch.keys;i>=0;--i){
                ch.child[i+1]=ch.child[i];
            }
        }
        ch.arr[0].key=arr[ind-1].key;
        ch.arr[0].value=arr[ind-1].value;

        if(leaf==false){
            ch.child[0]=sib.child[sib.keys];
        }
        arr[ind-1].key=sib.arr[sib.keys-1].key;
        arr[ind-1].value=sib.arr[sib.keys-1].value;
        ch.keys+=1;
        sib.keys-=1;
        return;
    }
    private void fillUnderflow(int ind){
        if(ind!=0 && child[ind-1].keys>=M/2)
            borrowFromprev(ind);
        else if(ind!=keys && child[ind+1].keys>=M/2){
            borrowFromnext(ind);
        }
        else{
            if(ind!=keys){
                merge(ind); 
            }
            else{
                merge(ind-1);
            }     
        }
        return; 
    }
    private Node<Key,Value> getNext(int ind){
        BTNode<Key,Value> curr=child[ind+1];
        while(curr.leaf==false){
            curr=curr.child[0];
        }
        return curr.arr[0];
    }
    private Node<Key,Value> getprev(int ind){
        BTNode<Key,Value> curr=child[ind];
        while(curr.leaf==false){
            curr=curr.child[curr.keys];
        }
        return curr.arr[curr.keys-1];
    }
    private void removeFromLeaf(int ind){
        for(int i=ind+1;i<keys;++i){
            arr[i-1].key=arr[i].key;
            arr[i-1].value=arr[i].value;
        }
        keys--;
        return;
    }
    private void removeFromNonLeaf(int ind){
        Comparable<Key> k=arr[ind].key;
        //Node n=arr[ind];
        if(child[ind].keys>=M/2){
            Node<Key,Value> prev=getprev(ind);
            arr[ind].key=prev.key;
            arr[ind].value=prev.value;
            child[ind].remove(prev.key);
        }
        else if(child[ind+1].keys>=M/2){
            Node<Key,Value> next=getNext(ind);
            arr[ind].key=next.key;
            arr[ind].value=next.value;
            child[ind+1].remove(next.key);
        }
        else{
            merge(ind);
            child[ind].remove(k);
        }
        return;
    }
    private void remove(Comparable<Key> key) {
        int ind= findkey(key);
        if(ind<keys && equalto(arr[ind].key,key))
        {
            if(leaf==true)
                removeFromLeaf(ind);
            else
                removeFromNonLeaf(ind);
        }
        else{
            if(leaf==true){
                System.out.println("No such Key");
                return;
            }
            boolean flag=false;
            if(ind==keys) flag=true;
            //else flag=false;
            //System.out.println("After NSK");
            //System.out.println(ind);
            if(child[ind].keys<M/2){
                fillUnderflow(ind);
            }
            //System.out.println("After NSK, if1");
            if(flag && ind>keys){    
                child[ind-1].remove(key);
                //System.out.println("After NSK, if1, if2 ");
            }
            else{
                child[ind].remove(key);
                //System.out.println("After NSK, if1, if2, else ");
            }    
        }
        return;

    }
    private int findkey(Comparable<Key> key){
        int ind=0;
        while(ind<keys && lessthan(arr[ind].key,key))
            ++ind;
        return ind;    
    }

}
	
    public BTree(int b) throws bNotEvenException {  
        /* Initializes an empty b-tree. Assume b is even. */
        //throw new RuntimeException("Not Implemented");
        if(b%2!=0) throw new bNotEvenException();
        M=b;
        root=null;
    }

    @Override
    public boolean isEmpty() {
        return n==0;
      //throw new RuntimeException("Not Implemented");
    }

    @Override
    public int size() {
        return n;
      //throw new RuntimeException("Not Implemented");
    }

    @Override
    public int height() {
    	return height;
        //throw new RuntimeException("Not Implemented");
    }

    @Override
    public List<Value> search(Key key) throws IllegalKeyException {
        if(root==null) return null;
        else{
            list=new Vector<>();
            root.search(key);
            return typecast(list);
        }
    }

    @Override
    public void insert(Key key, Value val) {
        //throw new RuntimeException("Not Implemented");
        if(root==null){
            root=new BTNode<Key,Value>(true);
            root.arr[0].key=key;
            root.arr[0].value=val;
            //System.out.println(root.arr[0].key);
            root.keys=1;
        }
        else{
            if(root.keys==M-1){
                BTNode<Key,Value> s=new BTNode<Key,Value>(false);
                s.child[0]=root;
                s.childsplit(0,root);
                int i=0;
                if(lessthan(s.arr[0].key,key)){
                    i++;
                }
                s.child[i].insertnonfull(key, val);
                root=s;
                height++;
            }
            else root.insertnonfull(key,val);
        }
        n++;
        return;
    }

    @Override
    public void delete(Key key) throws IllegalKeyException {
        //throw new RuntimeException("Not Implemented");
        occur=0;
        root.occur(key);
        //System.out.println("occur ="+occur);
        for(int i=0;i<occur;i++){
            if(root==null){
                System.out.println("Tree is empty");
            }
            root.remove(key);
            if(root.keys==0){
                //BTNode<Key,Value> temp=root;
                if(root.leaf){
                    root=null;
                }
                else{
                    root=root.child[0];
                }
                height--;
                //temp=null;
            }
            n--;
        }
        return;
    }
    public String toString(){
        if(root==null){
            String s="[]";
            return s;
        }
        else{
            traversal=" ";
            root.travel();
            //traversal.substring(0,traversal.length()-3)+"]";
            return traversal.substring(0,traversal.length()-2)+"]";
        }
    }
    private boolean lessthan(Comparable<Key> key1,Comparable<Key> key2){
        return key1.compareTo((Key) key2)<0;
    }
    private List<Value> typecast(List<Object> list){
        List<Value> lis=new Vector<>();
        for(Object obj: list){
            Value val=(Value) obj;
            lis.add(val);
        }
        return lis;
    }    
}
		