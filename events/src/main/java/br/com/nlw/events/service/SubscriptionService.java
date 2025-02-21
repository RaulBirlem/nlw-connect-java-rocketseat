package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepo;
import br.com.nlw.events.repository.SubscriptionRepo;
import br.com.nlw.events.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user){
        //recuperar evento pelo nome
        Event evt = evtRepo.findByPrettyName(eventName);
        if (evt == null){
            throw new EventNotFoundException("Evento "+eventName +" não existe.");
        }
        User userRec = userRepo.findByEmail(user.getEmail());
        if(userRec == null) { // usuário não cadastrado
            userRec = userRepo.save(user);
        }

        Subscription subs = new Subscription();

        subs.setEvent(evt);
        subs.setSubscriber(userRec);

        Subscription tmpSub = subRepo.findByEventAndSubscriber(evt,userRec);
        if (tmpSub != null){ //já tem inscrição
            throw new SubscriptionConflictException("Já existe inscrição para o usuário "+ userRec.getName() + " no evento "+evt.getTitle()+"!");
        }

        Subscription res = subRepo.save(subs);
        return new SubscriptionResponse(res.getSubscriptionNumber(),"http://codecraft.com/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId());
    }
}
