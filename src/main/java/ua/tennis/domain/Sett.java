package ua.tennis.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sett")
public class Sett implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "home_probability")
    private Double homeProbability;

    @Column(name = "away_probability")
    private Double awayProbability;

    @Column(name = "set_number")
    private Integer setNumber;

    @OneToMany(mappedBy = "sett")
    private Set<Game> games = new HashSet<>();

    @ManyToOne
    private Match match;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public Sett homeScore(Integer homeScore) {
        this.homeScore = homeScore;
        return this;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public Sett awayScore(Integer awayScore) {
        this.awayScore = awayScore;
        return this;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public Double getHomeProbability() {
        return homeProbability;
    }

    public Sett homeProbability(Double homeProbability) {
        this.homeProbability = homeProbability;
        return this;
    }

    public void setHomeProbability(Double homeProbability) {
        this.homeProbability = homeProbability;
    }

    public Double getAwayProbability() {
        return awayProbability;
    }

    public Sett awayProbability(Double awayProbability) {
        this.awayProbability = awayProbability;
        return this;
    }

    public void setAwayProbability(Double awayProbability) {
        this.awayProbability = awayProbability;
    }

    public Set<Game> getGames() {
        return games;
    }

    public Sett games(Set<Game> games) {
        this.games = games;
        return this;
    }

    public Sett addGame(Game game) {
        this.games.add(game);
        game.setSett(this);
        return this;
    }

    public Sett removeGame(Game game) {
        this.games.remove(game);
        game.setSett(null);
        return this;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    public Match getMatch() {
        return match;
    }

    public Sett match(Match match) {
        this.match = match;
        return this;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Integer getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sett sett = (Sett) o;
        if (sett.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sett.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "\n" +"Sett{" +
            "id=" + id +
            ", homeScore=" + homeScore +"\n" +
            ", awayScore=" + awayScore +"\n" +
            ", homeProbability=" + homeProbability +"\n" +
            ", awayProbability=" + awayProbability +"\n" +
            ", setNumber=" + setNumber + "\n" +
            ", games=" + games +"\n" +
            ", match=" + match +
            '}';
    }
}
