package ua.tennis.domain;

import ua.tennis.domain.enumeration.MatchStatus;
import ua.tennis.domain.enumeration.Winner;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "match")
public class Match implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "league_name")
    private String leagueName;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "winner")
    private Winner winner;

    @Column(name = "number_of_sets_to_win")
    private Integer numberOfSetsToWin;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<Sett> setts = new HashSet<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<Odds> odds = new HashSet<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<Bet> bets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLeagueName() {
        return leagueName;
    }

    public Match leagueName(String leagueName) {
        this.leagueName = leagueName;
        return this;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
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


    public Set<Odds> getOdds() {
        return odds;
    }

    public void setOdds(Set<Odds> odds) {
        this.odds = odds;
    }

    public Set<Bet> getBets() {
        return bets;
    }

    public void setBets(Set<Bet> bets) {
        this.bets = bets;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Match status(MatchStatus status) {
        this.status = status;
        return this;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public Match winner(Winner winner) {
        this.winner = winner;
        return this;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Match updatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public Integer getNumberOfSetsToWin() {
        return numberOfSetsToWin;
    }

    public void setNumberOfSetsToWin(Integer numberOfSetsToWin) {
        this.numberOfSetsToWin = numberOfSetsToWin;
    }

    public Match numberOfSetsToWin(Integer numberOfSetsToWin) {
        this.numberOfSetsToWin = numberOfSetsToWin;
        return this;
    }

    public boolean isScoreCorrect() {
        return homeScore.equals(numberOfSetsToWin) || awayScore.equals(numberOfSetsToWin);
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
        return "\n" +"Match{" +
            "id=" + id +"\n" +
            ", homeScore=" + homeScore +"\n" +
            ", awayScore=" + awayScore +"\n" +
            ", startDate=" + startDate +"\n" +
            ", name='" + name + '\'' +"\n" +
            ", updatedDate=" + updatedDate + "\n" +
            ", winner=" + winner + "\n" +
            ", status=" + status +
            '}';
    }
}
