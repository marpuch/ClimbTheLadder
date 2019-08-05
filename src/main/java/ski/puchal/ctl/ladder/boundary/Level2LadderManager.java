package ski.puchal.ctl.ladder.boundary;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ski.puchal.ctl.ladder.entity.AccumulatedLevel2LaddersBean;

/**
 * @author Marek Puchalski, Capgemini
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Level2LadderManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(Level2LadderManager.class);

    private static final long TIME_TO_GENERATE_LADDER_MILISEC = 5 * 60 * 1000L;
    private long timestamp = System.currentTimeMillis();

    public AccumulatedLevel2LaddersBean generate() {
        final long timestamp_now = System.currentTimeMillis();
        final int ladders = (int) ((timestamp_now - timestamp) / TIME_TO_GENERATE_LADDER_MILISEC);
        return new AccumulatedLevel2LaddersBean(ladders, timestamp_now);
    }

    public void validate(final AccumulatedLevel2LaddersBean bean) {
        if (bean.getTimestamp() < timestamp) {
            throw new LadderException("To old ladder definition. Please refresh to get the newest one.");
        }
        if (bean.getTimestamp() > System.currentTimeMillis()) {
            throw new LadderException("Ladder timestamp lies in the future?!?!");
        }
    }

    public String serialize(final AccumulatedLevel2LaddersBean bean) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return Base64.getEncoder().encodeToString(mapper.writeValueAsString(bean).getBytes(StandardCharsets.UTF_8));
        } catch (final JsonProcessingException e) {
            throw new LadderException("Something went really wrong: " + e.getMessage());
        }
    }

    public AccumulatedLevel2LaddersBean deserialize(final String s) {
        try {
            final byte[] rawBytes = Base64.getDecoder().decode(s);
            final String jsonToBe = new String(rawBytes, StandardCharsets.UTF_8);
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonToBe, AccumulatedLevel2LaddersBean.class);
        } catch (final Exception e) {
            LOGGER.warn("Something went wrong while deserializing the value: " + s, e);
            throw new LadderException("Don't do malicious things with the token please!");
        }

    }
}
