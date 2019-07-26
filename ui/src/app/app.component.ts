import {Component, OnInit} from '@angular/core';
import {QueryService} from "./services/query.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Climb The Ladder';
  public listItems;

  constructor(private queryService : QueryService) {}

  ngOnInit(): void {
    this.getListItems();
  }

  getListItems() {
    this.queryService.get().subscribe(
        data => { this.listItems = data },
        err => { console.error(err) },
        () => console.log("Items loaded")
    )
  }
}
