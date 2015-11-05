package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.Creatable;
import domain.Updatable;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class RedeemTransaction extends domain.Entity implements Serializable, Creatable, Updatable {
    private static final play.api.Logger logger = play.api.Logger.apply(RedeemTransaction.class);
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Required
	@ManyToOne
	public User user;
	
	@Required
	public Long objId;
	
	@Column(length = 1024)
    public String note;
	
	@Required
    public Boolean deleted = false;
	
    @Enumerated(EnumType.STRING)
	public RedeemType redeemType;
 
    @Enumerated(EnumType.STRING)
	public TransactionState transactionState;
 
    public static enum RedeemType {
    	GAME_GIFT
    }
    
    public static enum TransactionState {
    	REQUESTED,
    	PENDING_CONTACT,
    	PENDING_INFO,
    	COMPLETE,
    	CANCELLED,
    	PROBLEM,
    	OTHER
    }
    
	public RedeemTransaction() {}
	
	public static RedeemTransaction findById(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT t from RedeemTransaction t where id = ?1 and deleted = false");
	        q.setParameter(1, id);
	        return (RedeemTransaction) q.getSingleResult();
	    } catch (NoResultException e) {
	    	return null;
	    }
	}
	
	@Transactional
    public static List<RedeemTransaction> getPendingRedeemTransactions(User user, Long objId, RedeemType redeemType) {
        Query q = JPA.em().createQuery("Select t from RedeemTransaction t where user = ?1 and objId = ?2 and redeemType = ?3 and transactionState <> ?4 and deleted = false");
        q.setParameter(1, user);
        q.setParameter(2, objId);
        q.setParameter(3, redeemType);
        q.setParameter(4, TransactionState.COMPLETE);
        try {
            return (List<RedeemTransaction>)q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
	
	@Transactional
    public static List<RedeemTransaction> getRedeemTransactions(Long objId, RedeemType redeemType) {
		Query q = JPA.em().createQuery("Select t from RedeemTransaction t where objId = ?1 and redeemType = ?2 and deleted = false order by transactionState desc, CREATED_DATE");
        q.setParameter(1, objId);
        q.setParameter(2, redeemType);
        try {
            return (List<RedeemTransaction>)q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
