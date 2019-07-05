import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  url = 'http://localhost:9001/'; // 'https://fec-stage.pwcinternal.com/';

  constructor(private http: HttpClient) {
  }


  postMethod(files: FileList) {
    const fileToUpload = files.item(0);
    const formData = new FormData();
    formData.append('file', fileToUpload, fileToUpload.name);
    this.http.post(this.url + 'upload', formData).subscribe((val) => {
      console.log(val);
    });
    return false;
  }
}
