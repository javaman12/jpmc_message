package marks.demo.datastore;

public interface DatastoreInterface extends MessageProcessInterface {
	
	void addDatastoreActiveListener(DatestoreActiveListener datestoreActiveListener);
	void removeDatastoreActiveListener(DatestoreActiveListener datestoreActiveListener);
}
