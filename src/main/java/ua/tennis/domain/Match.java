package ua.tennis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Match.
 */
@Entity
@Table(name = "match")
public class Match implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "home_name")
    private String homeName;

    @Column(name = "away_name")
    private String awayName;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "name")
    private String name;

    @Column(name = "open_date")
    private Instant openDate;

    @Column(name = "league_name")
    private String leagueName;

    @Column(name = "league_id")
    private Long leagueId;

    @OneToMany(mappedBy = "match")
    private Set<Sett> setts = new HashSet<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<Odds> odds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Match identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getHomeName() {
        return homeName;
    }

    public Match homeName(String homeName) {
        this.homeName = homeName;
        return this;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getAwayName() {
        return awayName;
    }

    public Match awayName(String awayName) {
        this.awayName = awayName;
        return this;
    }

    public void setAwayName(String awayName) {
        this.awayName = awayName;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public Match homeScore(Integer homeScore) {
        this.homeScore = homeScore;
        return this;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public Match awayScore(Integer awayScore) {
        this.awayScore = awayScore;
        return this;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Match startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public Match name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getOpenDate() {
        return openDate;
    }

    public Match openDate(Instant openDate) {
        this.openDate = openDate;
        return this;
    }

    public void setOpenDate(Instant openDate) {
        this.openDate = openDate;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public Set<Sett> getSetts() {
        return setts;
    }

    public Match setts(Set<Sett> setts) {
        this.setts = setts;
        return this;
    }

    public Match addSett(Sett sett) {
        this.setts.add(sett);
        sett.setMatch(this);
        return this;
    }

    public Match removeSett(Sett sett) {
        this.setts.remove(sett);
        sett.setMatch(null);
        return this;
    }

    public void setSetts(Set<Sett> setts) {
        this.setts = setts;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove


    public Set<Odds> getOdds() {
        return odds;
    }

    public void setOdds(Set<Odds> odds) {
        this.odds = odds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Match match = (Match) o;
        if (match.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), match.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Match{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", homeName='" + getHomeName() + "'" +
            ", awayName='" + getAwayName() + "'" +
            ", homeScore=" + getHomeScore() +
            ", awayScore=" + getAwayScore() +
            ", startDate='" + getStartDate() + "'" +
            ", name='" + getName() + "'" +
            ", openDate='" + getOpenDate() + "'" +
            "}";
    }
}
