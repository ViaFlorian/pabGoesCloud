import { Directive, EventEmitter, HostListener, Output } from '@angular/core';

@Directive({
  selector: '[pab-drag-and-drop]',
})
export class DragAndDropDirective {
  @Output() fileDropped = new EventEmitter<File>();

  // Dragover listener
  @HostListener('dragover', ['$event'])
  onDragOver(evt: any) {
    evt.preventDefault();
    evt.stopPropagation();
  }

  // Dragleave listener
  @HostListener('dragleave', ['$event'])
  public onDragLeave(evt: any) {
    evt.preventDefault();
    evt.stopPropagation();
  }

  // Drop listener
  @HostListener('drop', ['$event'])
  public ondrop(evt: any) {
    evt.preventDefault();
    evt.stopPropagation();
    const files = evt.dataTransfer.files;
    if (files.length > 0) {
      this.fileDropped.emit(files[0]);
    }
  }
}
